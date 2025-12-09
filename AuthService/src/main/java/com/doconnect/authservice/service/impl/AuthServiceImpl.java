package com.doconnect.authservice.service.impl;

import com.doconnect.authservice.dto.JwtResponse;
import com.doconnect.authservice.dto.LoginRequest;
import com.doconnect.authservice.dto.LogoutRequest;
import com.doconnect.authservice.dto.RefreshTokenRequest;
import com.doconnect.authservice.dto.RegisterRequest;
import com.doconnect.authservice.dto.TokenValidationResponse;
import com.doconnect.authservice.entity.RefreshToken;
import com.doconnect.authservice.entity.UserCredential;
import com.doconnect.authservice.exception.AuthException;
import com.doconnect.authservice.repository.RefreshTokenRepository;
import com.doconnect.authservice.repository.UserCredentialRepository;
import com.doconnect.authservice.service.AuthService;
import com.doconnect.authservice.service.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserCredentialRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserCredentialRepository userRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public JwtResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("Email already registered");
        }

        UserCredential user = UserCredential.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole().toUpperCase())
                .build();

        userRepository.save(user);
        return issueTokens(user);
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        UserCredential user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("Invalid credentials"));

        if (!user.isActive()) {
            throw new AuthException("Account is deactivated");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid credentials");
        }

        return issueTokens(user);
    }

    @Override
    public JwtResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new AuthException("Refresh token not found"));

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new AuthException("Refresh token expired or revoked");
        }

        UserCredential user = stored.getUser();
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);
        return issueTokens(user);
    }

    @Override
    public TokenValidationResponse validateToken(String token) {
        try {
            String email = jwtService.extractEmail(token);
            UserCredential user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AuthException("User not found"));

            if (!jwtService.isTokenValid(token, user)) {
                throw new AuthException("Token invalid");
            }

            List<String> roles = jwtService.extractRoles(token);
            return TokenValidationResponse.builder()
                    .valid(true)
                    .email(email)
                    .role(jwtService.extractRole(token))
                    .roles(roles)
                    .expiresAt(jwtService.extractExpiration(token))
                    .build();
        } catch (Exception e) {
            return TokenValidationResponse.builder()
                    .valid(false)
                    .email(null)
                    .role(null)
                    .roles(List.of())
                    .expiresAt(0)
                    .build();
        }
    }

    @Override
    public void logout(LogoutRequest request, String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            throw new AuthException("Access token is required");
        }

        String email = jwtService.extractEmail(accessToken);
        UserCredential user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found"));

        if (request.isAllDevices()) {
            List<RefreshToken> activeTokens = refreshTokenRepository.findByUserAndRevokedFalse(user);
            if (activeTokens.isEmpty()) {
                return;
            }
            activeTokens.forEach(token -> token.setRevoked(true));
            refreshTokenRepository.saveAll(activeTokens);
            return;
        }

        if (!StringUtils.hasText(request.getRefreshToken())) {
            throw new AuthException("Refresh token is required for single-session logout");
        }

        RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new AuthException("Refresh token not found"));

        if (!stored.getUser().getId().equals(user.getId())) {
            throw new AuthException("Refresh token does not belong to caller");
        }

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);
    }

    private JwtResponse issueTokens(UserCredential user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        List<String> roles = jwtService.extractRoles(accessToken);
        String primaryRole = roles.stream().findFirst().orElse(jwtService.extractRole(accessToken));

        refreshTokenRepository.save(RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .expiresAt(Instant.ofEpochMilli(jwtService.extractExpiration(refreshToken)))
                .revoked(false)
                .build());

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.extractExpiration(accessToken))
                .tokenType("Bearer")
                .email(user.getEmail())
                .role(primaryRole)
                .roles(roles)
                .build();
    }
}
