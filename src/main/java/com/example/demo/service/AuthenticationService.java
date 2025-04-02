package com.example.demo.service;

import com.example.demo.enums.Role;
import com.example.demo.model.RefreshToken;
import com.example.demo.model.User;
import com.example.demo.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticationService {

    Logger logger = LoggerFactory.getLogger(DeviceDetectService.class);

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private DeviceDetectService deviceDetectService;
    @Autowired
    RateLimitService rateLimitService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${testing.app.isProduction}")
    private boolean isProduction;

    public ResponseEntity<?> register(
            User user,
            HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String key = "register:" + ip;
        if (!rateLimitService.tryConsume(key)) {
          return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("Error", "Too many requests"));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        userService.save(user);

        return ResponseEntity.ok(Map.of("Message", "Register successful"));
    }

    public ResponseEntity<?> login(
            User user,
            String userAgent,
            HttpServletRequest request,
            HttpServletResponse response) {

        // Rate Limit
        String ip = request.getRemoteAddr();
        String key = "login:" + ip;
        if (!rateLimitService.tryConsume(key)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("Error", "Too many requests"));
        }

        // Authenticate
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
        User persistedUser = userService.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete old tokens if had
        try {
            refreshTokenService.deleteTokenByUserId(persistedUser.getId());
        } catch (EntityNotFoundException e) {
            logger.info("No existing refresh token for user");
        }

        // Create tokens
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.
                createRefreshToken(persistedUser, jwtUtil.generateRefreshToken(userDetails));

        deviceDetectService.detect(userAgent, persistedUser);

        // Cookie set
        accessCookieCreate(accessToken, response);
        refreshCookieCreate(refreshToken.getToken(), response);

        return ResponseEntity.ok(Map.of("Message", "Login successful"));
    }

    public ResponseEntity<?> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        SecurityContextHolder.clearContext();

        String refreshToken = cookieCheck(request);
        if (refreshToken != null) {
            RefreshToken token = refreshTokenService.findByToken(refreshToken).orElseThrow();
            refreshTokenService.deleteTokenById(token.getId());
        }

        deleteCookie(response, "access_token");
        deleteCookie(response, "refresh_token");

        return ResponseEntity.ok(Map.of("Message", "Logged out successfully"));
    }

    public ResponseEntity<?> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshToken = cookieCheck(request);

        // Refresh Token Verify
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Refresh token is missing"));
        }
        Optional<RefreshToken> storedRefreshToken = refreshTokenService.findByToken(refreshToken);
        if (storedRefreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
        }
        refreshTokenService.verifyExpiration(storedRefreshToken.get());

        // Rate Limit
        String username = jwtUtil.extractUsername(refreshToken);
        String key = "refresh:" + username;
        if (!rateLimitService.tryConsume(key)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("Error", "Too many requests"));
        }

        // Generate Tokens
        UserDetails userDetails = userService.loadUserByUsername(username);
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);

        accessCookieCreate(newAccessToken, response);

        try {
            return ResponseEntity.ok(Map.of("access_token", newAccessToken));

        } catch (ExpiredJwtException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token has expired"));
        } catch (JwtException | IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid refresh token"));
        }
    }

    private void accessCookieCreate(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(isProduction);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtUtil.getACCESS_EXPIRATION_TIME() / 1000));
        response.addCookie(cookie);
    }

    private void refreshCookieCreate(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(isProduction);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtUtil.getREFRESH_EXPIRATION_TIME() / 1000));
        response.addCookie(cookie);
    }

    private void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(isProduction);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String cookieCheck(HttpServletRequest request) {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh_token")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        return refreshToken;
    }
}
