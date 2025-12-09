package com.doconnect.answerservice.service.impl;

import com.doconnect.answerservice.dto.AnswerApprovalRequest;
import com.doconnect.answerservice.dto.AnswerRequest;
import com.doconnect.answerservice.dto.AnswerResponse;
import com.doconnect.answerservice.dto.LikeUpdateRequest;
import com.doconnect.answerservice.entity.Answer;
import com.doconnect.answerservice.feign.QuestionServiceClient;
import com.doconnect.answerservice.feign.dto.QuestionSummary;
import com.doconnect.answerservice.mapper.AnswerMapper;
import com.doconnect.answerservice.repository.AnswerRepository;
import com.doconnect.answerservice.service.AnswerService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final AnswerMapper answerMapper;
    private final QuestionServiceClient questionServiceClient;

    @Override
    public AnswerResponse createAnswer(AnswerRequest request, String answeredBy) {
        ensureQuestionAcceptsAnswers(request.getQuestionId());
        Answer answer = new Answer();
        answer.setQuestionId(request.getQuestionId());
        answer.setContent(request.getContent());
        answer.setAnsweredBy(answeredBy);
        Answer saved = answerRepository.save(answer);
        return answerMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AnswerResponse getAnswer(Long id) {
        return answerMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnswerResponse> getAnswersByQuestion(Long questionId) {
        return answerRepository.findByQuestionId(questionId).stream()
                .map(answerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnswerResponse> getAnswersByUser(String userId) {
        return answerRepository.findByAnsweredBy(userId).stream()
                .map(answerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnswerResponse> getPendingAnswers() {
        return answerRepository.findByApprovedFalseOrderByCreatedAtAsc().stream()
                .map(answerMapper::toResponse)
                .toList();
    }

    @Override
    public AnswerResponse updateAnswer(Long id, AnswerRequest request, String requesterId, boolean isAdmin) {
        Answer answer = findById(id);
        if (!isAdmin && !answer.getAnsweredBy().equals(requesterId)) {
            throw new AccessDeniedException("Only owners or admins can edit an answer");
        }
        ensureQuestionAcceptsAnswers(request.getQuestionId());
        answer.setContent(request.getContent());
        answer.setQuestionId(request.getQuestionId());
        Answer saved = answerRepository.save(answer);
        return answerMapper.toResponse(saved);
    }

    @Override
    public AnswerResponse updateApproval(Long id, AnswerApprovalRequest request) {
        Answer answer = findById(id);
        answer.setApproved(request.isApproved());
        answer.setApprovedBy(request.getReviewerId());
        Answer saved = answerRepository.save(answer);
        return answerMapper.toResponse(saved);
    }

    @Override
    public AnswerResponse updateLikes(Long id, LikeUpdateRequest request) {
        Answer answer = findById(id);
        int delta = request.getDelta();
        int next = Math.max(0, answer.getLikeCount() + delta);
        answer.setLikeCount(next);
        Answer saved = answerRepository.save(answer);
        return answerMapper.toResponse(saved);
    }

    @Override
    public void deleteAnswer(Long id) {
        if (!answerRepository.existsById(id)) {
            throw new EntityNotFoundException("Answer not found");
        }
        answerRepository.deleteById(id);
    }

    private Answer findById(Long id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found"));
    }

    private void ensureQuestionAcceptsAnswers(Long questionId) {
        QuestionSummary summary = fetchQuestion(questionId);
        if (!summary.approved()) {
            throw new IllegalStateException("Question must be approved before posting answers");
        }
    }

    private QuestionSummary fetchQuestion(Long questionId) {
        try {
            QuestionSummary summary = questionServiceClient.getQuestionById(questionId);
            if (summary == null) {
                throw new EntityNotFoundException("Question not found");
            }
            return summary;
        } catch (FeignException.NotFound ex) {
            throw new EntityNotFoundException("Question not found");
        } catch (FeignException ex) {
            throw new IllegalStateException("Unable to verify question status", ex);
        }
    }
}
