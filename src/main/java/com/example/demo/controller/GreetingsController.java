package com.example.demo.controller;

import com.example.demo.service.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/greetings")
public class GreetingsController {

    @Autowired
    private RateLimitService rateLimitService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static final String GREETING_KEY = "greetings:default";
    private static final Duration TTL = Duration.ofSeconds(10);

    @GetMapping
    public ResponseEntity<String> greetings() {
        String key = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!rateLimitService.tryConsume(key)) {
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Rate limit exceeded");
        }

        // Cache Redis
        if (Boolean.TRUE.equals(redisTemplate.hasKey(GREETING_KEY))) {
            return ResponseEntity.ok(Objects.requireNonNull(redisTemplate.opsForValue().get(GREETING_KEY)));
        }

        String newGreeting = generateGreeting();
        redisTemplate.opsForValue().set(GREETING_KEY, newGreeting, TTL);
        return ResponseEntity.ok(newGreeting);
    }

    private String generateGreeting() {
        return "Hello, World! Current time: " + LocalDateTime.now();
    }
}
