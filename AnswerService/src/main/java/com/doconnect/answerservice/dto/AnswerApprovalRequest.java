package com.doconnect.answerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerApprovalRequest {

    private boolean approved;

    @NotBlank(message = "Reviewer id is required")
    private String reviewerId;
}
