package com.doconnect.questionservice.mapper;

import com.doconnect.questionservice.dto.QuestionRequest;
import com.doconnect.questionservice.dto.QuestionResponse;
import com.doconnect.questionservice.entity.Question;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class QuestionMapper {

    public Question toEntity(QuestionRequest request) {
        Question question = new Question();
        question.setTitle(request.getTitle());
        question.setDescription(request.getDescription());
        question.setTopic(request.getTopic());
        question.setAskedBy(request.getAskedBy());
        question.setTags(Collections.emptyList());
        return question;
    }

    public QuestionResponse toResponse(Question entity) {
        return QuestionResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .topic(entity.getTopic())
                .askedBy(entity.getAskedBy())
            .approved(entity.isApproved())
            .approvedBy(entity.getApprovedBy())
            .resolved(entity.isResolved())
            .resolvedBy(entity.getResolvedBy())
            .resolvedAt(entity.getResolvedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .tags(entity.getTags())
                .build();
    }
}
