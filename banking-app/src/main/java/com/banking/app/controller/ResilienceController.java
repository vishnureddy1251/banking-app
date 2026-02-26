package com.banking.app.controller;

import com.banking.app.service.ResilienceService;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;

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

}