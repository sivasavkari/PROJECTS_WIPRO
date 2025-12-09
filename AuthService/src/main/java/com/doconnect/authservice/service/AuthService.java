package com.doconnect.authservice.service;

import com.doconnect.authservice.dto.JwtResponse;
import com.doconnect.authservice.dto.LoginRequest;
import com.doconnect.authservice.dto.LogoutRequest;
import com.doconnect.authservice.dto.RefreshTokenRequest;
import com.doconnect.authservice.dto.RegisterRequest;
import com.doconnect.authservice.dto.TokenValidationResponse;

public interface AuthService {
    JwtResponse register(RegisterRequest request);
    JwtResponse login(LoginRequest request);
    JwtResponse refreshToken(RefreshTokenRequest request);
    TokenValidationResponse validateToken(String token);
    void logout(LogoutRequest request, String accessToken);
}
