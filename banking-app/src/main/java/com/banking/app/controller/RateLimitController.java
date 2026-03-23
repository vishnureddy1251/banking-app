package com.banking.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.Map;

@RestController
@Tag(name = "14. 🚦 Rate Limiting", description = "View rate limit policy")
@RequestMapping("/api/v1/rate-limit")
public class RateLimitController {

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getRateLimitInfo() {
        return ResponseEntity.ok(Map.of(
                "limit", "20 requests per minute per IP",
                "algorithm", "Token Bucket (Bucket4j)",
                "scope", "Per IP address",
                "appliesTo", "All /api/** endpoints",
                "responseWhenExceeded", "HTTP 429 - Too Many Requests",
                "headers", Map.of(
                        "X-Rate-Limit-Remaining", "Tokens left in your bucket",
                        "X-Rate-Limit-Limit", "Max tokens per minute"
                )
        ));
    }
}
