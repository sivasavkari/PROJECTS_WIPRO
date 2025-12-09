package com.doconnect.authservice.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class JwtResponse {
    String accessToken;
    String refreshToken;
    long expiresIn;
    String tokenType;
    String email;
    String role;
    List<String> roles;
}
