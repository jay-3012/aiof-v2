package com.tech2nxt.aiofbackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("AIOF Fitness API")
                        .version("1.0.0")
                        .description("""
                                REST API for AIOF Fitness App - A mindful fitness tracking application.
                                
                                Features:
                                - User authentication (JWT)
                                - Custom workout creation and scheduling
                                - Exercise library integration
                                - Workout session tracking with timers
                                - Mandatory journal entries after workouts
                                - Progress tracking and streak system
                                - Weight logging
                                
                                For Flutter model generation: Export this spec from /v3/api-docs
                                """)
                        .contact(new Contact()
                                .name("AIOF Team")
                                .email("support@aiof-fitness.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.aiof-fitness.com")
                                .description("Production Server (Update when deployed)")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token obtained from /api/auth/login")));
    }
}