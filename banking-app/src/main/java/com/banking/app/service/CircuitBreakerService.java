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

    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    public Map<String, Object> processPayment(Long accountId, BigDecimal amount, String description) {
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


    @CircuitBreaker(name = "loanService", fallbackMethod = "loanFallback")
    public Map<String, Object> processLoanApproval(Long loanId) {
        log.info("Processing loan approval through circuit breaker...");
        simulateExternalCall("Credit Check Service");
        return Map.of(
                "status", "APPROVED",
                "loanId", loanId,
                "message", "Loan approved after credit check",
                "timestamp", LocalDateTime.now().toString()
        );
    }

    public Map<String, Object> loanFallback(Long loanId, Throwable throwable) {
        log.warn("CIRCUIT BREAKER ACTIVATED for loan! Reason: {}", throwable.getMessage());
        return Map.of(
                "status", "PENDING",
                "loanId", loanId,
                "message", "Credit check service is down. Loan queued for manual review.",
                "fallback", true,
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @CircuitBreaker(name = "transferService", fallbackMethod = "transferFallback")
    public Map<String, Object> processExternalTransfer(Long fromAccount, Long toAccount,
                                                       BigDecimal amount) {
        log.info("Processing external transfer through circuit breaker...");
        simulateExternalCall("External Bank API");
        return Map.of(
                "status", "SUCCESS",
                "from", fromAccount,
                "to", toAccount,
                "amount", amount.toString(),
                "message", "External transfer completed",
                "timestamp", LocalDateTime.now().toString()
        );
    }

    public Map<String, Object> transferFallback(Long fromAccount, Long toAccount,
                                                BigDecimal amount, Throwable throwable) {
        log.warn("CIRCUIT BREAKER ACTIVATED for transfer! Reason: {}", throwable.getMessage());
        return Map.of(
                "status", "QUEUED",
                "from", fromAccount,
                "to", toAccount,
                "amount", amount.toString(),
                "message", "External bank service is down. Transfer queued and will be retried.",
                "fallback", true,
                "timestamp", LocalDateTime.now().toString()
        );
    }

    private void simulateExternalCall(String serviceName) {
        if (Math.random() < 0.4) {
            throw new RuntimeException(serviceName + " is not responding - connection timeout");
        }
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}