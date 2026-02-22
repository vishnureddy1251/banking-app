package com.banking.app.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitFilter {

    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    private static final int REQUESTS_PER_MINUTE = 20;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String endpoint = request.getRequestURI();

        if (!endpoint.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);

        Bucket bucket = bucketCache.computeIfAbsent(clientIp, k -> createBucket());

        if (bucket.tryConsume(1)) {
            // Token available → allow request
            long remainingTokens = bucket.getAvailableTokens();
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(remainingTokens));
            response.setHeader("X-Rate-Limit-Limit", String.valueOf(REQUESTS_PER_MINUTE));
            filterChain.doFilter(request, response);
        } else {
            // No tokens left → reject request
            log.warn("Rate limit exceeded for IP: {} on endpoint: {}", clientIp, endpoint);
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\":\"Too Many Requests\","
                            + "\"message\":\"Rate limit exceeded. Maximum " + REQUESTS_PER_MINUTE + " requests per minute.\","
                            + "\"retryAfterSeconds\":60}"
            );
        }
    }

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.classic(
                REQUESTS_PER_MINUTE,
                Refill.greedy(REQUESTS_PER_MINUTE, Duration.ofMinutes(1))
        );
        return Bucket.builder().addLimit(limit).build();
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