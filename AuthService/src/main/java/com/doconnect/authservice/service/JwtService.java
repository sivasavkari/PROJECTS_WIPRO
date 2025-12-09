package com.doconnect.authservice.service;

import com.doconnect.authservice.entity.UserCredential;

import java.util.List;

public interface JwtService {
    String generateAccessToken(UserCredential user);
    String generateRefreshToken(UserCredential user);
    String extractEmail(String token);
    String extractRole(String token);
    List<String> extractRoles(String token);
    boolean isTokenValid(String token, UserCredential user);
    long extractExpiration(String token);
}
