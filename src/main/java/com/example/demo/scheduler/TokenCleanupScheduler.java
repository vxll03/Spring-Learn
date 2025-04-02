package com.example.demo.scheduler;

import com.example.demo.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TokenCleanupScheduler {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

//    @Scheduled(cron = "0 0 0 * * ?")
//    @Transactional
//    public void cleanupExpiredTokens() {
//        Instant now = Instant.now();
//        refreshTokenRepository.deleteByRevokedTrueOrExpireDateBefore(now);
//    }
}
