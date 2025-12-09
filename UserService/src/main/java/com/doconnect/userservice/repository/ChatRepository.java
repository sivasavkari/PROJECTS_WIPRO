package com.doconnect.userservice.repository;

import com.doconnect.userservice.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
    List<ChatMessage> findByReceiverIdAndSenderId(Long receiverId, Long senderId);
}

