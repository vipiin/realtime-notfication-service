package com.vipin.notification_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisMessageSubscriber {
@Autowired
private SimpMessagingTemplate messagingTemplate;
public void onMessage(String message){
    System.out.println("Received from Redis:" + message);
    messagingTemplate.convertAndSend("/topic/messages",message);
}
}
