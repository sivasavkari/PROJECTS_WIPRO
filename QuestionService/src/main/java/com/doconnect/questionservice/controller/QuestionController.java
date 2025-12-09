package com.doconnect.questionservice.controller;

import com.doconnect.questionservice.dto.QuestionApprovalRequest;
import com.doconnect.questionservice.dto.QuestionRequest;
import com.doconnect.questionservice.dto.QuestionResolutionRequest;
import com.doconnect.questionservice.dto.QuestionResponse;
import com.doconnect.questionservice.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestBody QuestionRequest request) {
        QuestionResponse response = questionService.createQuestion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<QuestionResponse> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<List<QuestionResponse>> getQuestions(
            @RequestParam(value = "topic", required = false) String topic,
            @RequestParam(value = "userId", required = false) String userId) {
        if (topic != null) {
            return ResponseEntity.ok(questionService.getQuestionsByTopic(topic));
        }
        if (userId != null) {
            return ResponseEntity.ok(questionService.getQuestionsByUser(userId));
        }
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @GetMapping("/resolved")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<List<QuestionResponse>> getResolvedQuestions() {
        return ResponseEntity.ok(questionService.getResolvedQuestions());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<QuestionResponse>> getAllQuestionsAdmin() {
        return ResponseEntity.ok(questionService.getAllQuestionsAdmin());
    }

    @GetMapping("/admin/pending")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<QuestionResponse>> getPendingQuestions() {
        return ResponseEntity.ok(questionService.getPendingQuestions());
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<List<QuestionResponse>> searchQuestions(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(questionService.searchQuestions(keyword));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<QuestionResponse> updateQuestion(@PathVariable Long id,
                                                           @Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.ok(questionService.updateQuestion(id, request));
    }

    @PutMapping("/{id}/approval")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<QuestionResponse> updateApproval(@PathVariable Long id,
                                                           @Valid @RequestBody QuestionApprovalRequest request) {
        return ResponseEntity.ok(questionService.updateApproval(id, request));
    }

    @PutMapping("/{id}/resolution")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<QuestionResponse> updateResolution(@PathVariable Long id,
                                                             @Valid @RequestBody QuestionResolutionRequest request) {
        return ResponseEntity.ok(questionService.updateResolution(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
