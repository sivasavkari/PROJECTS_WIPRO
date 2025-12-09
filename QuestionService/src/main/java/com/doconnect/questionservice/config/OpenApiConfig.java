package com.doconnect.questionservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI questionServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DoConnect Question Service API")
                        .version("v1")
                        .description("Operations related to questions"));
    }
}
