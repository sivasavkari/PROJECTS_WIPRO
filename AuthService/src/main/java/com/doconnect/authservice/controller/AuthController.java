package com.doconnect.authservice.controller;

import com.doconnect.authservice.dto.JwtResponse;
import com.doconnect.authservice.dto.LoginRequest;
import com.doconnect.authservice.dto.LogoutRequest;
import com.doconnect.authservice.dto.RefreshTokenRequest;
import com.doconnect.authservice.dto.RegisterRequest;
import com.doconnect.authservice.dto.TokenValidationResponse;
import com.doconnect.authservice.exception.AuthException;
import com.doconnect.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(201).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @Valid @RequestBody LogoutRequest request) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthException("Authorization header missing");
        }
        String accessToken = authHeader.substring(7);
        authService.logout(request, accessToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validate(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(authService.validateToken(token));
    }
}
