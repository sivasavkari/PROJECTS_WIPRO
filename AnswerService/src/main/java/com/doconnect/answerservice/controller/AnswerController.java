package com.doconnect.answerservice.controller;

import com.doconnect.answerservice.dto.AnswerApprovalRequest;
import com.doconnect.answerservice.dto.AnswerRequest;
import com.doconnect.answerservice.dto.AnswerResponse;
import com.doconnect.answerservice.dto.LikeUpdateRequest;
import com.doconnect.answerservice.service.AnswerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
@Tag(name = "Answers", description = "Operations for managing answers")
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<AnswerResponse> createAnswer(@Valid @RequestBody AnswerRequest request,
                                                       Authentication authentication) {
        AnswerResponse response = answerService.createAnswer(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<AnswerResponse> updateAnswer(@PathVariable Long id,
                                                       @Valid @RequestBody AnswerRequest request,
                                                       Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        AnswerResponse response = answerService.updateAnswer(id, request, authentication.getName(), isAdmin);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<AnswerResponse> getAnswer(@PathVariable Long id) {
        return ResponseEntity.ok(answerService.getAnswer(id));
    }

    @GetMapping("/question/{questionId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<List<AnswerResponse>> getByQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok(answerService.getAnswersByQuestion(questionId));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<List<AnswerResponse>> getByUser(@PathVariable String userId) {
        return ResponseEntity.ok(answerService.getAnswersByUser(userId));
    }

    @GetMapping("/admin/pending")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<AnswerResponse>> getPendingAnswers() {
        return ResponseEntity.ok(answerService.getPendingAnswers());
    }

    @PatchMapping("/{id}/approval")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AnswerResponse> updateApproval(@PathVariable Long id,
                                                         @Valid @RequestBody AnswerApprovalRequest request) {
        return ResponseEntity.ok(answerService.updateApproval(id, request));
    }

    @PatchMapping("/{id}/likes")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<AnswerResponse> updateLikes(@PathVariable Long id,
                                                      @Valid @RequestBody LikeUpdateRequest request) {
        return ResponseEntity.ok(answerService.updateLikes(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id) {
        answerService.deleteAnswer(id);
        return ResponseEntity.noContent().build();
    }
}
