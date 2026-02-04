package com.vipin.notification_service.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vipin.notification_service.model.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long>{
    List<ChatMessage> findTop50ByOrderByCreatedAtDesc();
}
