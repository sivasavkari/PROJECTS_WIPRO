package com.doconnect.questionservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor jwtRelayInterceptor() {
        return template -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            String authHeader = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
            if (StringUtils.hasText(authHeader)) {
                template.header(HttpHeaders.AUTHORIZATION, authHeader);
            }
        };
    }
}
