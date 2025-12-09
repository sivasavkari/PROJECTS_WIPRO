package com.doconnect.questionservice.service.impl;

import com.doconnect.questionservice.dto.ChatMessageRequest;
import com.doconnect.questionservice.dto.ChatMessageResponse;
import com.doconnect.questionservice.entity.ChatMessage;
import com.doconnect.questionservice.repository.ChatMessageRepository;
import com.doconnect.questionservice.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private static final int MAX_LIMIT = 100;

    private final ChatMessageRepository chatMessageRepository;

    @Override
    public ChatMessageResponse sendMessage(String roomId, ChatMessageRequest request) {
        ChatMessage message = ChatMessage.builder()
                .roomId(normalizeRoom(roomId))
                .senderId(request.getSenderId())
                .senderName(request.getSenderName())
                .content(request.getContent())
                .build();

        return toResponse(chatMessageRepository.save(message));
    }

    @Override
    public List<ChatMessageResponse> fetchRecentMessages(String roomId, int limit) {
        if (limit <= 0) {
            limit = 20;
        }
        limit = Math.min(limit, MAX_LIMIT);

        List<ChatMessage> messages = chatMessageRepository
                .findByRoomIdOrderBySentAtDesc(normalizeRoom(roomId), PageRequest.of(0, limit));

        if (messages.isEmpty()) {
            return Collections.emptyList();
        }

        List<ChatMessageResponse> responses = messages.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        Collections.reverse(responses);
        return responses;
    }

    private ChatMessageResponse toResponse(ChatMessage message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .roomId(message.getRoomId())
                .senderId(message.getSenderId())
                .senderName(message.getSenderName())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .build();
    }

    private String normalizeRoom(String roomId) {
        return roomId == null ? "general" : roomId.trim().toLowerCase();
    }
}
