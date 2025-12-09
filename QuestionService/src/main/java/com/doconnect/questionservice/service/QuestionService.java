package com.doconnect.questionservice.service;

import com.doconnect.questionservice.dto.QuestionApprovalRequest;
import com.doconnect.questionservice.dto.QuestionRequest;
import com.doconnect.questionservice.dto.QuestionResolutionRequest;
import com.doconnect.questionservice.dto.QuestionResponse;

import java.util.List;

public interface QuestionService {

    QuestionResponse createQuestion(QuestionRequest request);

    QuestionResponse getQuestionById(Long id);

    List<QuestionResponse> getQuestionsByTopic(String topic);

    List<QuestionResponse> getQuestionsByUser(String userId);

    List<QuestionResponse> getAllQuestions();

    List<QuestionResponse> getAllQuestionsAdmin();

    List<QuestionResponse> getResolvedQuestions();

    List<QuestionResponse> searchQuestions(String keyword);

    List<QuestionResponse> getPendingQuestions();

    QuestionResponse updateApproval(Long id, QuestionApprovalRequest request);

    QuestionResponse updateResolution(Long id, QuestionResolutionRequest request);

    QuestionResponse updateQuestion(Long id, QuestionRequest request);

    void deleteQuestion(Long id);
}
