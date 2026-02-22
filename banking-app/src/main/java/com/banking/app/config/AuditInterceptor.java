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

}
