package com.vipin.notification_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired 
    StringRedisTemplate redisTemplate;

    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public void handleMessage(@Payload String message){
        System.out.println("Received: "+ message+" on instance: "+ System.getProperty("server.port", "8080"));
        redisTemplate.convertAndSend("chat", message);
        // simpMessagingTemplate.convertAndSend("/topic/messages",message);
    }
}
