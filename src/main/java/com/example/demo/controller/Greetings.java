package com.example.demo.controller;

import com.example.demo.service.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/greetings")
public class Greetings {

    @Autowired
    private RateLimitService rateLimitService;

    @GetMapping
    public ResponseEntity<Map<String, String>> greetings() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String key = username;
        if (!rateLimitService.tryConsume(key)) {
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Rate limit exceeded"));
        }

        return ResponseEntity.ok(Map.of("message", "Hello world!"));
    }
}
