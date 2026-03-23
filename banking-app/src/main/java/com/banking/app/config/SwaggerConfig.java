package com.banking.app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI bankingAppOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🏦 Banking Application API")
                        .description("""
                                ## A Production-Grade Banking REST API
                                
                                **How to use:**
                                1. Register a user using `/api/auth/register`
                                2. Login using `/api/auth/login` → copy the token
                                3. Click the 🔒 **Authorize** button above → paste `Bearer <your-token>`
                                4. Now you can test all endpoints!
                                
                                **Roles:**
                                - `ROLE_USER` → Can view accounts, deposit, withdraw, transfer, pay bills
                                - `ROLE_ADMIN` → Everything above + delete accounts, approve/reject loans, view audit logs
                                
                                **Features:** JWT Auth • Rate Limiting (20 req/min) • Circuit Breaker • Event Sourcing • GraphQL • WebSocket • Caching
                                """)
                        .version("2.0")
                        .contact(new Contact()
                                .name("Vishnu Reddy")
                                .url("https://github.com/vishnureddy1251"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}