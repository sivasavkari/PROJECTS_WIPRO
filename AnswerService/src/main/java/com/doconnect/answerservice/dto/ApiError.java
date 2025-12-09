package com.doconnect.answerservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

@Getter
@Builder
public class ApiError {
    @Builder.Default
    private final Instant timestamp = Instant.now();
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    @Builder.Default
    private final Map<String, String> validationErrors = Collections.emptyMap();
}
