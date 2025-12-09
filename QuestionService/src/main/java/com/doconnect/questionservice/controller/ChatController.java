package com.doconnect.questionservice.controller;

import com.doconnect.questionservice.dto.ChatMessageRequest;
import com.doconnect.questionservice.dto.ChatMessageResponse;
import com.doconnect.questionservice.service.ChatMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/rooms/{roomId}/messages")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable String roomId,
            @RequestParam(name = "limit", defaultValue = "50") int limit) {
        return ResponseEntity.ok(chatMessageService.fetchRecentMessages(roomId, limit));
    }

    @PostMapping("/rooms/{roomId}/messages")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<ChatMessageResponse> postMessage(@PathVariable String roomId,
                                                           @Valid @RequestBody ChatMessageRequest request) {
        ChatMessageResponse response = chatMessageService.sendMessage(roomId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
