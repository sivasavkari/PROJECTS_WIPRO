package com.doconnect.questionservice.feign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserProfileClientResponse(
        Long id,
        String authUserId,
        String displayName,
        boolean active,
        Instant createdAt
) {
}
