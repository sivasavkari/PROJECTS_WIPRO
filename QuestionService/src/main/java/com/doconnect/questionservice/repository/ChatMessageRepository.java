package com.doconnect.questionservice.repository;

import com.doconnect.questionservice.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByRoomIdOrderBySentAtDesc(String roomId, Pageable pageable);
}
