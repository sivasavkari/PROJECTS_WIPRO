package com.doconnect.questionservice.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class ChatMessageResponse {
    Long id;
    String roomId;
    String senderId;
    String senderName;
    String content;
    Instant sentAt;
}
