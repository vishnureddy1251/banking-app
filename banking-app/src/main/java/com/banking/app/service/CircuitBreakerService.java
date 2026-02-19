package com.banking.app.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CircuitBreakerService {

    private final PaymentGatewayService paymentGatewayService;

    @CircuitBreaker(name = "paymentService", fallbackMethod = "PaymentFallback")
    public Map<String, Object> processPayment(Long accountId, BigDecimal amount, String description){
        log.info("Processing payment through circuit breaker...");
        return paymentGatewayService.processPayment(accountId, amount, description);
    }

    public Map<String, Object> paymentFallback(Long accountId, BigDecimal amount,
                                               String description, Throwable throwable) {
        log.warn("CIRCUIT BREAKER ACTIVATED for payment! Reason: {}", throwable.getMessage());

        return Map.of(
                "status", "FAILED",
                "message", "Payment service is temporarily unavailable. Please try again later.",
                "fallback", true,
                "reason", throwable.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );
    }
}
