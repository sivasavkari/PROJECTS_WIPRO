package com.doconnect.answerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeUpdateRequest {

    @NotNull(message = "Delta is required")
    private Integer delta;
}
