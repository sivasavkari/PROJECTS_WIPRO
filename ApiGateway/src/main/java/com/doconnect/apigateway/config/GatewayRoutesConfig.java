package com.doconnect.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder, JwtAuthenticationGatewayFilter authFilter) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/auth/**")
                        .filters(f -> f.filter(authFilter.apply(new JwtAuthenticationGatewayFilter.Config())))
                        .uri("lb://AUTH-SERVICE"))
                .route("user-service", r -> r
                        .path("/users/**")
                        .filters(f -> f.filter(authFilter.apply(new JwtAuthenticationGatewayFilter.Config())))
                        .uri("lb://USER-SERVICE"))
                .route("question-service", r -> r
                        .path("/questions/**")
                        .filters(f -> f.filter(authFilter.apply(new JwtAuthenticationGatewayFilter.Config())))
                        .uri("lb://QUESTION-SERVICE"))
                .route("answer-service", r -> r
                        .path("/answers/**")
                        .filters(f -> f.filter(authFilter.apply(new JwtAuthenticationGatewayFilter.Config())))
                        .uri("lb://ANSWER-SERVICE"))
                .route("admin-service", r -> r
                        .path("/admin/**")
                        .filters(f -> {
                            JwtAuthenticationGatewayFilter.Config adminConfig = new JwtAuthenticationGatewayFilter.Config();
                            adminConfig.setRequireAdmin(true);
                            return f.filter(authFilter.apply(adminConfig));
                        })
                        .uri("lb://ADMIN-SERVICE"))
                .build();
    }
}
