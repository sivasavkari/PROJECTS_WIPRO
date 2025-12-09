package com.doconnect.questionservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRequest {

    @NotBlank(message = "Sender id is required")
    @Size(max = 150, message = "Sender id must be at most 150 characters")
    private String senderId;

    @NotBlank(message = "Sender name is required")
    @Size(max = 150, message = "Sender name must be at most 150 characters")
    private String senderName;

    @NotBlank(message = "Message is required")
    @Size(max = 500, message = "Message must be at most 500 characters")
    private String content;
}
