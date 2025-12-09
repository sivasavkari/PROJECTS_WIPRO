package com.doconnect.questionservice.service;

import com.doconnect.questionservice.dto.ChatMessageRequest;
import com.doconnect.questionservice.dto.ChatMessageResponse;

import java.util.List;

public interface ChatMessageService {

    ChatMessageResponse sendMessage(String roomId, ChatMessageRequest request);

    List<ChatMessageResponse> fetchRecentMessages(String roomId, int limit);
}
