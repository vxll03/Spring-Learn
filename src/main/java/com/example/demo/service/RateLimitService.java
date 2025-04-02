package com.example.demo.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {
    private final Map<String, Bucket> bucketMap = new ConcurrentHashMap<>();

    public boolean tryConsume(String key) {
        Bucket bucket = bucketMap.computeIfAbsent(key, this::createBucket);
        return bucket.tryConsume(1);
    }

    private Bandwidth createLimit(
            Integer capacity,
            Integer tokensPerInterval,
            Duration interval) {
        if (tokensPerInterval > capacity) {
            throw new IllegalArgumentException("Tokens per interval cannot exceed capacity");
        }

        return Bandwidth.builder()
                .capacity(capacity)
                .refillIntervally(tokensPerInterval, interval)
                .build();
    }

    private Bucket createBucket(String key) {
        Bandwidth limit = switch (key.split(":")[0]) {
            case "login" -> createLimit(10, 1, Duration.ofSeconds(6));
            case "register" -> createLimit(5, 1, Duration.ofMinutes(12));
            case "refresh" -> createLimit(10, 1, Duration.ofSeconds(6));
            default -> createLimit(10, 1, Duration.ofSeconds(6));
        };
        return Bucket.builder().addLimit(limit).build();
    }
}
