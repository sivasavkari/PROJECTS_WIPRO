package com.doconnect.questionservice.service.impl;

import com.doconnect.questionservice.dto.QuestionApprovalRequest;
import com.doconnect.questionservice.dto.QuestionRequest;
import com.doconnect.questionservice.dto.QuestionResolutionRequest;
import com.doconnect.questionservice.dto.QuestionResponse;
import com.doconnect.questionservice.entity.Question;
import com.doconnect.questionservice.feign.UserServiceClient;
import com.doconnect.questionservice.feign.dto.UserProfileClientResponse;
import com.doconnect.questionservice.mapper.QuestionMapper;
import com.doconnect.questionservice.repository.QuestionRepository;
import com.doconnect.questionservice.service.NotificationService;
import com.doconnect.questionservice.service.QuestionService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final UserServiceClient userServiceClient;
    private final NotificationService notificationService;

    @Override
    public QuestionResponse createQuestion(QuestionRequest request) {
        validateAuthorIsActive(request.getAskedBy());
        Question question = questionMapper.toEntity(request);
        Question saved = questionRepository.save(question);
        notificationService.notifyQuestionCreated(saved);
        return questionMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponse getQuestionById(Long id) {
        Question question = findQuestion(id);
        return questionMapper.toResponse(question);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByTopic(String topic) {
        return questionRepository.findByTopicIgnoreCaseAndApprovedTrue(topic).stream()
                .map(questionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByUser(String userId) {
        return questionRepository.findByAskedByAndApprovedTrue(userId).stream()
                .map(questionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> getAllQuestions() {
        return questionRepository.findByApprovedTrueOrderByCreatedAtDesc().stream()
                .map(questionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> getAllQuestionsAdmin() {
        return questionRepository.findAll().stream()
                .map(questionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> getResolvedQuestions() {
        return questionRepository.findByApprovedTrueAndResolvedTrueOrderByResolvedAtDesc().stream()
                .map(questionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> searchQuestions(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllQuestions();
        }
        return questionRepository.searchByKeyword(keyword.trim()).stream()
                .map(questionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionResponse updateQuestion(Long id, QuestionRequest request) {
        Question question = findQuestion(id);
        question.setTitle(request.getTitle());
        question.setDescription(request.getDescription());
        question.setTopic(request.getTopic());
        question.setAskedBy(request.getAskedBy());
        Question updated = questionRepository.save(question);
        return questionMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> getPendingQuestions() {
        return questionRepository.findByApprovedFalseOrderByCreatedAtAsc().stream()
                .map(questionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionResponse updateApproval(Long id, QuestionApprovalRequest request) {
        Question question = findQuestion(id);
        question.setApproved(request.isApproved());
        question.setApprovedBy(request.getReviewerId());
        if (!request.isApproved()) {
            question.setResolved(false);
            question.setResolvedBy(null);
            question.setResolvedAt(null);
        }
        Question saved = questionRepository.save(question);
        notificationService.notifyQuestionApproved(saved);
        return questionMapper.toResponse(saved);
    }

    @Override
    public QuestionResponse updateResolution(Long id, QuestionResolutionRequest request) {
        Question question = findQuestion(id);
        if (!question.isApproved()) {
            throw new IllegalStateException("Question must be approved before resolving");
        }
        question.setResolved(request.isResolved());
        if (request.isResolved()) {
            question.setResolvedBy(request.getResolverId());
            question.setResolvedAt(Instant.now());
        } else {
            question.setResolvedBy(null);
            question.setResolvedAt(null);
        }
        Question saved = questionRepository.save(question);
        notificationService.notifyQuestionResolutionChanged(saved);
        return questionMapper.toResponse(saved);
    }

    @Override
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new EntityNotFoundException("Question not found");
        }
        questionRepository.deleteById(id);
    }

    private Question findQuestion(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));
    }

    private void validateAuthorIsActive(String askedBy) {
        UserProfileClientResponse profile = fetchUserProfile(askedBy);
        if (!profile.active()) {
            throw new IllegalStateException("User account is inactive");
        }
    }

    private UserProfileClientResponse fetchUserProfile(String authUserId) {
        try {
            UserProfileClientResponse response = userServiceClient.getUserProfile(authUserId);
            if (response == null) {
                throw new EntityNotFoundException("User not found");
            }
            return response;
        } catch (FeignException.NotFound ex) {
            throw new EntityNotFoundException("User not found");
        } catch (FeignException ex) {
            throw new IllegalStateException("Unable to verify user profile", ex);
        }
    }
}
