package com.doconnect.answerservice.feign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestionSummary(
        Long id,
        String title,
        boolean approved,
        boolean resolved,
        String askedBy,
        Instant createdAt
) {
}
