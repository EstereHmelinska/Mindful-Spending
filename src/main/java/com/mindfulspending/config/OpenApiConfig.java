package com.mindfulspending.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Provides basic metadata (title, version, description) shown at the top
// of the Swagger UI page. Optional, but makes the generated docs look intentional
// rather than auto-generated defaults.
@Configuration
public class OpenApiConfig {
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";
    @Bean
    public OpenAPI mindfulSpendingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mindful Spending API")
                        .version("0.0.1")
                        .description(
                                "Your ultimate expense tracker"))
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                );
    }
}
