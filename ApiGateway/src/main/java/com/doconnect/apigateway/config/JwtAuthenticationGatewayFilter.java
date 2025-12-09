package com.doconnect.apigateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthenticationGatewayFilter extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilter.Config> {

    private SecretKey secretKey;

    public JwtAuthenticationGatewayFilter() {
        super(Config.class);
    }

    @Value("${security.jwt.secret}")
    public void setSecret(String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Allow public paths without token (configured via Config.publicPaths)
            String path = request.getPath().value();
            if (config.publicPaths.stream().anyMatch(path::startsWith)) {
                return chain.filter(exchange);
            }

            List<String> authHeaders = request.getHeaders().getOrEmpty("Authorization");
            if (authHeaders.isEmpty() || !authHeaders.get(0).startsWith("Bearer ")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization header");
            }

            String token = authHeaders.get(0).substring(7);
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                if (Boolean.TRUE.equals(config.requireAdmin) && extractRoles(claims).stream()
                        .noneMatch(role -> role.equalsIgnoreCase("ROLE_ADMIN"))) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role required");
                }

            } catch (Exception ex) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            }

            return chain.filter(exchange);
        };
    }

    private List<String> extractRoles(Claims claims) {
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

    public static class Config {
        private List<String> publicPaths = List.of("/auth", "/users/register", "/users/login");
        private Boolean requireAdmin = false;

        public List<String> getPublicPaths() {
            return publicPaths;
        }

        public void setPublicPaths(List<String> publicPaths) {
            this.publicPaths = publicPaths;
        }

        public Boolean getRequireAdmin() {
            return requireAdmin;
        }

        public void setRequireAdmin(Boolean requireAdmin) {
            this.requireAdmin = requireAdmin;
        }
    }
}
