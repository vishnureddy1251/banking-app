package com.banking.app.config;

import com.banking.app.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditInterceptor implements HandlerInterceptor{

    private final AuditLogService auditLogService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {

        String endpoint = request.getRequestURI();

        if (!endpoint.startsWith("/api/")) {
            return;
        }

        String username = "ANONYMOUS";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            username = auth.getName();
        }

        String httpMethod = request.getMethod();
        String action = mapHttpMethodToAction(httpMethod);

        String entityType = extractEntityType(endpoint);

        String description = httpMethod + " " + endpoint;
        if (ex != null) {
            description += " - ERROR: " + ex.getMessage();
        }

        String ipAddress = getClientIp(request);

        auditLogService.logAction(
                username,
                action,
                entityType,
                null,
                description,
                httpMethod,
                endpoint,
                ipAddress,
                response.getStatus()
        );
    }

    private String mapHttpMethodToAction(String method) {
        return switch (method.toUpperCase()) {
            case "POST" -> "CREATE";
            case "GET" -> "READ";
            case "PUT" -> "UPDATE";
            case "DELETE" -> "DELETE";
            default -> method;
        };
    }

    private String extractEntityType(String endpoint) {
        if (endpoint.contains("/accounts")) return "ACCOUNT";
        if (endpoint.contains("/transactions")) return "TRANSACTION";
        if (endpoint.contains("/customers")) return "CUSTOMER";
        if (endpoint.contains("/loans")) return "LOAN";
        if (endpoint.contains("/bills")) return "BILL_PAYMENT";
        if (endpoint.contains("/notifications")) return "NOTIFICATION";
        if (endpoint.contains("/auth")) return "AUTH";
        if (endpoint.contains("/circuit-breaker")) return "CIRCUIT_BREAKER";
        return "UNKNOWN";
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
