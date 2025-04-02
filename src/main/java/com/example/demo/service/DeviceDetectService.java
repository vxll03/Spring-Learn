package com.example.demo.service;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class DeviceDetectService {
    public String detect(String device) {
        device = String.valueOf(device);
        System.out.println(device);
        if (device.contains("Mobile")) {
            System.out.println("Moblie");
            return "Mobile";
        } else if (device.contains("Windows")) {
            System.out.println("Windows");
            return "Windows";
        } else if (device.contains("Mac")) {
            System.out.println("Mac");
            return "Mac";
        }
        System.out.println("Failed");
        return "Failed";
    }
}
