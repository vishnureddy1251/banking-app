package com.banking.app.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.*;

import com.banking.app.service.ResilienceService;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/resilience")
@RequiredArgsConstructor
public class ResilienceController {

    private final ResilienceService resilienceService;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;
    private final BulkheadRegistry bulkheadRegistry;

    @PostMapping("/payment")
    public ResponseEntity<Map<String, Object>> testPayment(@RequestBody Map<String, Object> request) {
        Long accountId = Long.valueOf(request.get("accountId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String description = request.getOrDefault("description", "Test payment").toString();

        Map<String, Object> result = resilienceService.processPaymentWithResilience(
                accountId, amount, description);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> testTransfer(@RequestBody Map<String, Object> request) {
        Long fromAccount = Long.valueOf(request.get("fromAccountId").toString());
        Long toAccount = Long.valueOf(request.get("toAccountId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        Map<String, Object> result = resilienceService.processTransferWithResilience(
                fromAccount, toAccount, amount);
        return ResponseEntity.ok(result);
    }

        @PostMapping("/loan/{loanId}")
    public ResponseEntity<Map<String, Object>> testLoan(@PathVariable Long loanId) {
        Map<String, Object> result = resilienceService.processLoanWithResilience(loanId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();

        Map<String, Object> cbStatus = new HashMap<>();
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(cb -> {
            cbStatus.put(cb.getName(), Map.of(
                    "state", cb.getState().toString(),
                    "failureRate", cb.getMetrics().getFailureRate() + "%",
                    "totalCalls", cb.getMetrics().getNumberOfBufferedCalls(),
                    "failedCalls", cb.getMetrics().getNumberOfFailedCalls()
            ));
        });
        status.put("circuitBreakers", cbStatus);

        Map<String, Object> retryStatus = new HashMap<>();
        retryRegistry.getAllRetries().forEach(retry -> {
            retryStatus.put(retry.getName(), Map.of(
                    "successWithoutRetry", retry.getMetrics().getNumberOfSuccessfulCallsWithoutRetryAttempt(),
                    "successWithRetry", retry.getMetrics().getNumberOfSuccessfulCallsWithRetryAttempt(),
                    "failedWithRetry", retry.getMetrics().getNumberOfFailedCallsWithRetryAttempt(),
                    "failedWithoutRetry", retry.getMetrics().getNumberOfFailedCallsWithoutRetryAttempt()
            ));
        });
        status.put("retries", retryStatus);

        Map<String, Object> bhStatus = new HashMap<>();
        bulkheadRegistry.getAllBulkheads().forEach(bh -> {
            bhStatus.put(bh.getName(), Map.of(
                    "availableConcurrentCalls", bh.getMetrics().getAvailableConcurrentCalls(),
                    "maxAllowedConcurrentCalls", bh.getMetrics().getMaxAllowedConcurrentCalls()
            ));
        });
        status.put("bulkheads", bhStatus);

        status.put("retryAttempts", resilienceService.getResilienceStats());

        return ResponseEntity.ok(status);
    }

}
