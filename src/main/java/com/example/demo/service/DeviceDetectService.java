package com.example.demo.service;

import com.example.demo.model.Device;
import com.example.demo.model.User;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceDetectService {

    @Autowired
    DeviceService deviceService;
    @Autowired
    UserService userService;

    public void detect(String device, User user) {
        Logger logger = LoggerFactory.getLogger(DeviceDetectService.class);
        UserAgentAnalyzer analyzer = UserAgentAnalyzer.newBuilder()
                .hideMatcherLoadStats()
                .withCache(10000)
                .build();

        UserAgent agent = analyzer.parse(device);

        Device dev = new Device();
        dev.setUser(user);
        dev.setAgent(agent.getValue("AgentName"));
        dev.setDevice(agent.getValue("OperatingSystemName"));

        userService.save(user);
        deviceService.saveDevice(dev);

        logger.info(device);
    }
}
