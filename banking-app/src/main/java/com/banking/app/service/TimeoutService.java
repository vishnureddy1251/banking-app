package com.banking.app.service;

import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeoutService {

    private final PaymentGatewayService paymentGatewayService;

    @TimeLimiter(name = "paymentTimeout", fallbackMethod = "paymentTimeoutFallback")
    public CompletableFuture<Map<String, Object>> processPaymentWithTimeout(
            Long accountId, BigDecimal amount) {

        return CompletableFuture.supplyAsync(() -> {
            log.info("Processing payment for account {} - ${} (timeout: 3s)", accountId, amount);

            Map<String, Object> result = paymentGatewayService.processPayment(
                    accountId, amount, "Payment with timeout");

            log.info("Payment completed within timeout for account {}", accountId);
            return result;
        });
    }

    @TimeLimiter(name = "transferTimeout", fallbackMethod = "transferTimeoutFallback")
    public CompletableFuture<Map<String, Object>> processTransferWithTimeout(
            Long fromAccount, Long toAccount, BigDecimal amount) {

        return CompletableFuture.supplyAsync(() -> {
            log.info("Processing transfer {} → {} - ${} (timeout: 5s)",
                    fromAccount, toAccount, amount);

            simulateExternalCall(4000);

            return Map.of(
                    "status", "SUCCESS",
                    "from", fromAccount,
                    "to", toAccount,
                    "amount", amount.toString(),
                    "message", "External transfer completed within timeout",
                    "timestamp", LocalDateTime.now().toString()
            );
        });
    }

    @TimeLimiter(name = "loanTimeout", fallbackMethod = "loanTimeoutFallback")
    public CompletableFuture<Map<String, Object>> checkCreditWithTimeout(Long loanId) {

        return CompletableFuture.supplyAsync(() -> {
            log.info("Running credit check for loan {} (timeout: 10s)", loanId);

            simulateExternalCall(7000);

            return Map.of(
                    "status", "APPROVED",
                    "loanId", loanId,
                    "creditScore", 750,
                    "message", "Credit check completed within timeout",
                    "timestamp", LocalDateTime.now().toString()
            );
        });
    }

    @TimeLimiter(name = "paymentTimeout", fallbackMethod = "paymentTimeoutFallback")
    public CompletableFuture<Map<String, Object>> simulateSlowPayment(
            Long accountId, BigDecimal amount, int delayMs) {

        return CompletableFuture.supplyAsync(() -> {
            log.info("Simulating slow payment ({}ms delay, 3s timeout)", delayMs);

            simulateExternalCall(delayMs);

            return Map.of(
                    "status", "SUCCESS",
                    "message", "Payment completed in " + delayMs + "ms",
                    "accountId", accountId,
                    "timestamp", LocalDateTime.now().toString()
            );
        });
    }

    public CompletableFuture<Map<String, Object>> paymentTimeoutFallback(
            Long accountId, BigDecimal amount, Throwable throwable) {
        log.warn("PAYMENT TIMEOUT for account {}! Reason: {}", accountId, throwable.getMessage());
        return CompletableFuture.completedFuture(Map.of(
                "status", "TIMEOUT",
                "message", "Payment timed out after 3 seconds. Please try again.",
                "fallback", "TIMEOUT",
                "accountId", accountId,
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    private void simulateExternalCall(int delayMs) {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Call interrupted");
        }
    }
}
