package com.doconnect.answerservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class AnswerResponse {
    private Long id;
    private Long questionId;
    private String answeredBy;
    private String content;
    private boolean approved;
    private String approvedBy;
    private int likeCount;
    private Instant createdAt;
    private Instant updatedAt;
}
