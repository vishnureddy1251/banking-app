package com.banking.app.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResilienceService {

    private final PaymentGatewayService paymentGatewayService;

    private final AtomicInteger paymentRetryCount = new AtomicInteger(0);
    private final AtomicInteger transferRetryCount = new AtomicInteger(0);

    @Bulkhead(name = "paymentBulkhead", fallbackMethod = "bulkheadFallback")
    @Retry(name = "paymentRetry", fallbackMethod = "retryFallback")
    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    public Map<String, Object> processPaymentWithResilience(Long accountId, BigDecimal amount,
                                                            String description) {
        int attempt = paymentRetryCount.incrementAndGet();
        log.info("Payment attempt #{} for account {} - ${}", attempt, accountId, amount);

        Map<String, Object> result = paymentGatewayService.processPayment(accountId, amount, description);

        paymentRetryCount.set(0);
        return result;
    }

    @Bulkhead(name = "transferBulkhead", fallbackMethod = "bulkheadFallback")
    @Retry(name = "transferRetry", fallbackMethod = "retryFallback")
    @CircuitBreaker(name = "transferService", fallbackMethod = "transferFallback")
    public Map<String, Object> processTransferWithResilience(Long fromAccount, Long toAccount,
                                                             BigDecimal amount) {
        int attempt = transferRetryCount.incrementAndGet();
        log.info("Transfer attempt #{} from {} to {} - ${}", attempt, fromAccount, toAccount, amount);

        simulateExternalCall("External Bank API");

        transferRetryCount.set(0);
        return Map.of(
                "status", "SUCCESS",
                "from", fromAccount,
                "to", toAccount,
                "amount", amount.toString(),
                "message", "External transfer completed",
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @Bulkhead(name = "loanBulkhead", fallbackMethod = "bulkheadFallback")
    @CircuitBreaker(name = "loanService", fallbackMethod = "loanFallback")
    public Map<String, Object> processLoanWithResilience(Long loanId) {
        log.info("Processing loan approval with resilience for loan {}", loanId);

        simulateExternalCall("Credit Check Service");

        return Map.of(
                "status", "APPROVED",
                "loanId", loanId,
                "message", "Loan approved after credit check",
                "timestamp", LocalDateTime.now().toString()
        );
    }

    public Map<String, Object> paymentFallback(Long accountId, BigDecimal amount,
                                               String description, Throwable throwable) {
        log.warn("CIRCUIT BREAKER for payment! Reason: {}", throwable.getMessage());
        paymentRetryCount.set(0);
        return Map.of(
                "status", "FAILED",
                "message", "Payment service unavailable after all retries. Please try later.",
                "fallback", "CIRCUIT_BREAKER",
                "reason", throwable.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );
    }

    public Map<String, Object> retryFallback(Long accountId, BigDecimal amount,
                                             String description, Throwable throwable) {
        log.warn("ALL RETRIES EXHAUSTED for payment! Reason: {}", throwable.getMessage());
        paymentRetryCount.set(0);
        return Map.of(
                "status", "FAILED",
                "message", "Payment failed after 3 retries with exponential backoff.",
                "fallback", "RETRY_EXHAUSTED",
                "reason", throwable.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );
    }

    public Map<String, Object> transferFallback(Long fromAccount, Long toAccount,
                                                BigDecimal amount, Throwable throwable) {
        log.warn("CIRCUIT BREAKER for transfer! Reason: {}", throwable.getMessage());
        transferRetryCount.set(0);
        return Map.of(
                "status", "QUEUED",
                "from", fromAccount,
                "to", toAccount,
                "amount", amount.toString(),
                "message", "Transfer queued. External bank service is down.",
                "fallback", "CIRCUIT_BREAKER",
                "timestamp", LocalDateTime.now().toString()
        );
    }

}
