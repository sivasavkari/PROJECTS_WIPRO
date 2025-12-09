package com.doconnect.authservice.service.impl;

import com.doconnect.authservice.config.JwtProperties;
import com.doconnect.authservice.entity.UserCredential;
import com.doconnect.authservice.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class JwtServiceImpl implements JwtService {

    private final JwtProperties properties;
    private final SecretKey key;

    public JwtServiceImpl(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateAccessToken(UserCredential user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(properties.getAccessTokenMinutes(), ChronoUnit.MINUTES);
        List<String> roles = buildRoles(user);
        return Jwts.builder()
                .setSubject(user.getEmail())
            .claim("role", roles.get(0))
            .claim("roles", roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(UserCredential user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(properties.getRefreshTokenDays(), ChronoUnit.DAYS);
        List<String> roles = buildRoles(user);
        return Jwts.builder()
                .setSubject(user.getEmail())
            .claim("role", roles.get(0))
            .claim("roles", roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    @Override
    public String extractRole(String token) {
        return extractRoles(token).stream().findFirst().orElse(null);
    }

    @Override
    public List<String> extractRoles(String token) {
        return resolveRoles(getClaims(token));
    }

    @Override
    public boolean isTokenValid(String token, UserCredential user) {
        Claims claims = getClaims(token);
        return claims.getSubject().equals(user.getEmail()) && claims.getExpiration().after(new Date());
    }

    @Override
    public long extractExpiration(String token) {
        return getClaims(token).getExpiration().getTime();
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private List<String> buildRoles(UserCredential user) {
        return List.of(normalizeRole(user.getRole()));
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "ROLE_USER";
        }
        String normalized = role.trim().toUpperCase();
        return normalized.startsWith("ROLE_") ? normalized : "ROLE_" + normalized;
    }

    private List<String> resolveRoles(Claims claims) {
        Object rolesClaim = claims.get("roles");
        if (rolesClaim instanceof List<?> roleList && !roleList.isEmpty()) {
            return roleList.stream()
                    .map(String::valueOf)
                    .map(String::trim)
                    .filter(value -> !value.isEmpty())
                    .toList();
        }
        String singleRole = claims.get("role", String.class);
        if (singleRole != null && !singleRole.isBlank()) {
            return List.of(singleRole.trim());
        }
        return List.of();
    }
}
