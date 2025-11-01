package com.tech2nxt.aiofbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AiofBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiofBackendApplication.class, args);
        System.out.println("""
            
            ╔═══════════════════════════════════════════╗
            ║   AIOF Fitness API Started Successfully  ║
            ║   Swagger UI: http://localhost:8080/swagger-ui.html
            ║   API Docs: http://localhost:8080/v3/api-docs
            ╚═══════════════════════════════════════════╝
            """);
    }

}
