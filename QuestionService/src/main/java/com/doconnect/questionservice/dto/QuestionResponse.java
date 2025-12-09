package com.doconnect.questionservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
public class QuestionResponse {
    private Long id;
    private String title;
    private String description;
    private String topic;
    private String askedBy;
    private boolean approved;
    private String approvedBy;
    private boolean resolved;
    private String resolvedBy;
    private Instant resolvedAt;
    private Instant createdAt;
    private Instant updatedAt;
    private List<String> tags;
}
