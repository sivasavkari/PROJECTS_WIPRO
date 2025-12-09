package com.doconnect.answerservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerRequest {

    @NotNull(message = "Question id is required")
    @Min(value = 1, message = "Question id must be positive")
    private Long questionId;

    @NotBlank(message = "Content is required")
    private String content;
}
