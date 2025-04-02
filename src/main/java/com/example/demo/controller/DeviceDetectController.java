package com.example.demo.controller;

import com.example.demo.service.DeviceDetectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/device-tests")
public class DeviceDetectController {

    @Autowired
    DeviceDetectService deviceDetectService;

    @GetMapping
    public ResponseEntity<String> request(@RequestHeader(value = "User-Agent") String userAgent) {
        return ResponseEntity.ok(deviceDetectService.detect(userAgent));
    }
}
