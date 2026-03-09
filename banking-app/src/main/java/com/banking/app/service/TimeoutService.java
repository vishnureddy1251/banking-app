package com.banking.app.service;

import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
}
