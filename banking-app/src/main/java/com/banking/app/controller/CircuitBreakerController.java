package com.banking.app.controller;

import com.banking.app.service.CircuitBreakerService;
import com.banking.app.service.PaymentGatewayService;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/loan/{loanId}")
    public ResponseEntity<Map<String, Object>> testLoanApproval(@PathVariable Long loanId) {
        Map<String, Object> result = circuitBreakerService.processLoanApproval(loanId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> testTransfer(@RequestBody Map<String, Object> request) {
        Long fromAccount = Long.valueOf(request.get("fromAccountId").toString());
        Long toAccount = Long.valueOf(request.get("toAccountId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        Map<String, Object> result = circuitBreakerService.processExternalTransfer(
                fromAccount, toAccount, amount);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/gateway/down")
    public ResponseEntity<Map<String, String>> gatewayDown() {
        paymentGatewayService.enableFailureMode();
        return ResponseEntity.ok(Map.of(
                "message", "Payment Gateway is now DOWN (failure mode enabled)",
                "status", "DOWN"
        ));
    }

    @PostMapping("/gateway/up")
    public ResponseEntity<Map<String, String>> gatewayUp() {
        paymentGatewayService.disableFailureMode();
        return ResponseEntity.ok(Map.of(
                "message", "Payment Gateway has RECOVERED (failure mode disabled)",
                "status", "UP"
        ));
    }

    @GetMapping("/gateway/status")
    public ResponseEntity<Map<String, Object>> gatewayStatus() {
        return ResponseEntity.ok(Map.of(
                "gatewayUp", !paymentGatewayService.isFailureModeEnabled(),
                "failureMode", paymentGatewayService.isFailureModeEnabled()
        ));
    }
}
