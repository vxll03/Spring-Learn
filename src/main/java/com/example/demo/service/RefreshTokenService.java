package com.example.demo.service;

import com.example.demo.model.RefreshToken;
import com.example.demo.model.User;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenService {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    UserService userService;

    public RefreshToken createRefreshToken(User user, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpireDate(Instant.now().plusMillis(jwtUtil.getREFRESH_EXPIRATION_TIME()));

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public void revokeToken(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    public void verifyExpiration(RefreshToken token) {
        if (token.getExpireDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token has expired");
        }
    }

    public boolean isTokenValid(String token) {
        try {
            return jwtUtil.isTokenValid(token, userService.loadUserByUsername(jwtUtil.extractUsername(token)));
        } catch (Exception ex) {
            return false;
        }
    }
}
