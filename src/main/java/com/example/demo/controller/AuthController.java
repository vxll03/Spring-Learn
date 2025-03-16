    package com.example.demo.controller;

    import com.example.demo.model.RefreshToken;
    import com.example.demo.model.User;
    import com.example.demo.service.RefreshTokenService;
    import com.example.demo.service.UserService;
    import com.example.demo.util.JwtUtil;
    import io.jsonwebtoken.ExpiredJwtException;
    import io.jsonwebtoken.JwtException;
    import jakarta.servlet.ServletRequest;
    import jakarta.servlet.http.Cookie;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.AuthenticationException;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.web.bind.annotation.*;

    import java.util.Map;
    import java.util.Optional;
    import java.util.Set;

    @RestController
    @RequestMapping("/api/auth")
    public class AuthController {

        @Autowired
        private AuthenticationManager authenticationManager;
        @Autowired
        private JwtUtil jwtUtil;
        @Autowired
        private UserService userService;
        @Autowired
        PasswordEncoder passwordEncoder;
        @Autowired
        RefreshTokenService refreshTokenService;

        @Value("${testing.app.isProduction}")
        private boolean isProduction;

        @PostMapping("/register")
        public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Set.of("ROLE_USER"));
            userService.save(user);
            return ResponseEntity.ok(Map.of("message", "User created"));
        }


        @PostMapping("/login")
        public ResponseEntity<Map<String, String>> login(@RequestBody User user, HttpServletResponse response)
                throws Exception {
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
                );
            } catch (AuthenticationException ex) {
                System.out.println("Ошибка: " + ex);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "user credentials not found"));
            }
            UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
            User persistedUser = userService.findByUsername(user.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String accessToken = jwtUtil.generateAccessToken(userDetails);
            RefreshToken refreshToken = refreshTokenService.
                    createRefreshToken(persistedUser, jwtUtil.generateRefreshToken(userDetails));
            System.out.println("access token = " + accessToken);
            System.out.println("refresh token = " + refreshToken);

            //Create cookie
            Cookie cookie = new Cookie("access_token", accessToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(isProduction);
            cookie.setPath("/");
            cookie.setMaxAge((int) (jwtUtil.getACCESS_EXPIRATION_TIME() / 1000));
            response.addCookie(cookie);

            //Refresh token cookie
            Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken.getToken());
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(isProduction);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge((int) (jwtUtil.getREFRESH_EXPIRATION_TIME() / 1000));
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok(Map.of(
                    "access_token", accessToken,
                    "refresh_token", refreshToken.getToken()
            ));
        }

        @PostMapping("/logout")
        public ResponseEntity<Map<String, String>> logout(
                HttpServletRequest request,
                HttpServletResponse response) {
            SecurityContextHolder.clearContext();

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

            if (refreshToken != null) {
                Optional<RefreshToken> tokenOpt = refreshTokenService.findByToken(refreshToken);
                tokenOpt.ifPresent(token -> refreshTokenService.revokeToken(token));
            }

            deleteCookie(response, "access_token");
            deleteCookie(response, "refresh_token");

            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        }

        @PostMapping("/refresh")
        public ResponseEntity<Map<String, String>> refreshToken(
                HttpServletRequest request,
                HttpServletResponse response) {
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

            if (refreshToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Refresh token is missing"));
            }

            Optional<RefreshToken> storedRefreshToken = refreshTokenService.findByToken(refreshToken);
            if (storedRefreshToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
            }

            if (!refreshTokenService.isTokenValid(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
            }
            refreshTokenService.verifyExpiration(storedRefreshToken.get());

            String username = jwtUtil.extractUsername(refreshToken);
            UserDetails userDetails = userService.loadUserByUsername(username);
            String newAccessToken = jwtUtil.generateAccessToken(userDetails);

            Cookie accessTokenCookie = new Cookie("access_token", newAccessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(isProduction);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge((int) (jwtUtil.getACCESS_EXPIRATION_TIME() / 1000));
            response.addCookie(accessTokenCookie);

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

        private void deleteCookie(HttpServletResponse response, String cookieName) {
            Cookie cookie = new Cookie(cookieName, null);
            cookie.setHttpOnly(true);
            cookie.setSecure(isProduction);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }