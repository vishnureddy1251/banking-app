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
                                .url("https://opensource.org/licenses/MIT")))
                .tags(List.of(
                        new Tag().name("1. 🔐 Authentication").description("Register and login to get JWT token. START HERE!"),
                        new Tag().name("2. 💰 Accounts").description("Create, view, deposit, withdraw, and transfer money"),
                        new Tag().name("3. 💰 Accounts V2").description("Enhanced account endpoints with formatted balances and validation"),
                        new Tag().name("4. 📜 Transactions").description("View transaction history for any account"),
                        new Tag().name("5. 👤 Customers").description("Manage customer profiles"),
                        new Tag().name("6. 🏦 Loans").description("Apply, approve, reject, and repay loans"),
                        new Tag().name("7. 💡 Bill Payments").description("Pay bills and track payments"),
                        new Tag().name("8. 🔔 Notifications").description("View and manage notifications"),
                        new Tag().name("9. ⚡ Circuit Breaker").description("Test circuit breaker pattern with simulated failures"),
                        new Tag().name("10. 🛡️ Resilience").description("Test retry + bulkhead + circuit breaker combined"),
                        new Tag().name("11. 📦 Event Sourcing").description("Publish events, view history, rebuild state, time travel"),
                        new Tag().name("12. 🗄️ Cache").description("View cache stats and clear caches"),
                        new Tag().name("13. 📊 Audit Logs").description("View API activity logs (ADMIN only)"),
                        new Tag().name("14. 🚦 Rate Limiting").description("View rate limit policy"),
                        new Tag().name("15. ⏱️ System Patterns").description("Test timeouts and write batching"),
                        new Tag().name("16. 🔌 WebSocket Test").description("Trigger real-time WebSocket notifications")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Token"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Token",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Paste your JWT token here (without 'Bearer ' prefix)")));
    }
}