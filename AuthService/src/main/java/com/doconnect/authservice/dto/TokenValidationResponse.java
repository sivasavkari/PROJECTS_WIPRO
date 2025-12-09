package com.doconnect.authservice.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class TokenValidationResponse {
    boolean valid;
    String email;
    String role;
    List<String> roles;
    long expiresAt;
}
