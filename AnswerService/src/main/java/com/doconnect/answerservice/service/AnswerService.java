package com.doconnect.answerservice.service;

import com.doconnect.answerservice.dto.AnswerApprovalRequest;
import com.doconnect.answerservice.dto.AnswerRequest;
import com.doconnect.answerservice.dto.AnswerResponse;
import com.doconnect.answerservice.dto.LikeUpdateRequest;

import java.util.List;

public interface AnswerService {

    AnswerResponse createAnswer(AnswerRequest request, String answeredBy);

    AnswerResponse getAnswer(Long id);

    List<AnswerResponse> getAnswersByQuestion(Long questionId);

    List<AnswerResponse> getAnswersByUser(String userId);

    List<AnswerResponse> getPendingAnswers();

    AnswerResponse updateAnswer(Long id, AnswerRequest request, String requesterId, boolean isAdmin);

    AnswerResponse updateApproval(Long id, AnswerApprovalRequest request);

    AnswerResponse updateLikes(Long id, LikeUpdateRequest request);

    void deleteAnswer(Long id);
}
