package com.doconnect.answerservice.mapper;

import com.doconnect.answerservice.dto.AnswerResponse;
import com.doconnect.answerservice.entity.Answer;
import org.springframework.stereotype.Component;

@Component
public class AnswerMapper {

    public AnswerResponse toResponse(Answer answer) {
        return AnswerResponse.builder()
                .id(answer.getId())
                .questionId(answer.getQuestionId())
                .answeredBy(answer.getAnsweredBy())
                .content(answer.getContent())
                .approved(answer.isApproved())
                .approvedBy(answer.getApprovedBy())
                .likeCount(answer.getLikeCount())
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .build();
    }
}
