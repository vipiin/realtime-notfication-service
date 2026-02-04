package com.vipin.notification_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vipin.notification_service.model.ChatMessage;

@Service
public class RedisMessageSubscriber implements MessageListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(@NonNull Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());

        System.out.println("Received from Redis channel [" + channel + "]: " + body);

        if ("presence".equals(channel)) {
            messagingTemplate.convertAndSend("/topic/presence", body);
        } else if ("notifications".equals(channel)) {
            try {
                // Expecting JSON { "user": "vipin", "message": "hello" }
                NotificationData data = objectMapper.readValue(body, NotificationData.class);
                System.out.println("Routing notification to user: " + data.user);
                messagingTemplate.convertAndSendToUser(data.user, "/topic/notifications", data.message);
            } catch (Exception e) {
                System.err.println("Failed to parse Notification JSON: " + e.getMessage());
            }
        } else {
            try {
                ChatMessage chatMessage = objectMapper.readValue(body, ChatMessage.class);
                messagingTemplate.convertAndSend("/topic/messages", chatMessage);
            } catch (Exception e) {
                // Fallback: If it's not valid JSON or parsing fails, send as raw string
                System.err.println("Failed to parse Redis message as JSON: " + e.getMessage());
                messagingTemplate.convertAndSend("/topic/messages", body);
            }
        }
    }

    // Helper class for parsing
    static class NotificationData {
        public String user;
        public String message;
    }
}
