package com.vipin.notification_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vipin.notification_service.Repository.ChatMessageRepository;
import com.vipin.notification_service.model.ChatMessage;

@Service
public class MessageService {
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(String sender,String message){
        ChatMessage chatMessage=new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setMessage(message);

        return chatMessageRepository.save(chatMessage);
    }
    public List<ChatMessage> getRecentMessages(){
        return chatMessageRepository.findTop50ByOrderByCreatedAtDesc();
    }

}
