package com.vipin.notification_service.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public String handleMessage(@Payload String message){
        System.out.println("Received: "+ message+" on instance: "+ System.getProperty("server.port", "8080"));
        return message;
    }
}
