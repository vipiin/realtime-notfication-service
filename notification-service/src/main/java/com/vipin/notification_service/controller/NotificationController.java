package com.vipin.notification_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vipin.notification_service.model.ChatMessage;
import com.vipin.notification_service.service.MessageService;

@Controller
public class NotificationController {

    @GetMapping("/v1/health")
    @ResponseBody
    public String health() {
        return "UP";
    }

    @Autowired
    MessageService messageService;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${server.port}")
    private String serverPort;

    @MessageMapping("/send")
    public void handleMessage(@Payload String message,
            SimpMessageHeaderAccessor headerAccessor) throws JsonProcessingException {
        String sender = getUsername(headerAccessor);

        System.out.println("Received: " + message + " on instance: " + serverPort);

        ChatMessage savedMessage = messageService.saveMessage(sender, message);

        String jsonMessage = objectMapper.writeValueAsString(savedMessage);
        if (jsonMessage != null) {
            redisTemplate.convertAndSend("chat", jsonMessage);
        }
    }

    @MessageMapping("/typing")
    public void handleTyping(@Payload String status, SimpMessageHeaderAccessor headerAccessor) {
        String username = getUsername(headerAccessor);
        String typingMessage = username + ":" + status;
        redisTemplate.convertAndSend("presence", typingMessage);
    }

    /**
     * Endpoint to simulate a notification being sent to a specific user.
     * In a real app, this would be called by another service (e.g., Order Service).
     */
    @GetMapping("/api/notify")
    @ResponseBody
    public String notifyUser(String user, String message) throws JsonProcessingException {
        NotificationPayload payload = new NotificationPayload(user, message);
        String json = objectMapper.writeValueAsString(payload);
        redisTemplate.convertAndSend("notifications", json);
        return "Notification sent to " + user;
    }

    private String getUsername(SimpMessageHeaderAccessor headerAccessor) {
        String sender = "Anonymous";
        if (headerAccessor != null && headerAccessor.getSessionAttributes() != null) {
            sender = (String) headerAccessor.getSessionAttributes().get("username");
        }
        return sender != null ? sender : "Anonymous";
    }

    @GetMapping("/api/history")
    @ResponseBody
    public List<ChatMessage> getHistory() {
        return messageService.getRecentMessages();
    }

    // Inner class for notification payload
    static class NotificationPayload {
        public String user;
        public String message;

        public NotificationPayload(String user, String message) {
            this.user = user;
            this.message = message;
        }
    }
}
