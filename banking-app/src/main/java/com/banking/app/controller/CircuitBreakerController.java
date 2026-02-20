package com.banking.app.controller;

import com.banking.app.service.CircuitBreakerService;
import com.banking.app.service.PaymentGatewayService;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/circuit-breaker")
@RequiredArgsConstructor
public class CircuitBreakerController {

    private final CircuitBreakerService circuitBreakerService;
    private final PaymentGatewayService paymentGatewayService;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @PostMapping("/payment")
    public ResponseEntity<Map<String, Object>> testPayment(@RequestBody Map<String, Object> request) {
        Long accountId = Long.valueOf(request.get("accountId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String description = request.getOrDefault("description", "Test payment").toString();

        Map<String, Object> result = circuitBreakerService.processPayment(accountId, amount, description);
        return ResponseEntity.ok(result);
    }
}
