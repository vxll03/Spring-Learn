package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DeviceDetectService {
    public String detect(String device) {
        Logger logger = LoggerFactory.getLogger(DeviceDetectService.class);
        device = String.valueOf(device);
        System.out.println(device);
        if (device.contains("Mobile")) {
            logger.info("Moblie");
            return "Mobile";
        } else if (device.contains("Windows")) {
            logger.info("Windows");
            return "Windows";
        } else if (device.contains("Mac")) {
            logger.info("Mac");
            return "Mac";
        }
        logger.warn("Failed");
        return "Failed";
    }
}
