package com.doconnect.questionservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionResolutionRequest {

    private boolean resolved;

    @NotBlank(message = "Resolver id is required")
    private String resolverId;
}
