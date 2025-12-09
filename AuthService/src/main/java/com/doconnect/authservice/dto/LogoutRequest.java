package com.doconnect.authservice.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LogoutRequest {

    private boolean allDevices = false;

    @Size(max = 512, message = "Refresh token must be 512 characters or less")
    private String refreshToken;
}
