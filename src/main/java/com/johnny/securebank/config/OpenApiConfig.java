package com.johnny.securebank.config;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI secureBankOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("SecureBankCore API")
                        .description("REST API for a secure banking backend built with Spring Boot.")
                        .version("1.0.0"));
        return openAPI;
    }
}
