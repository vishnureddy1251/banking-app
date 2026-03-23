package com.banking.app.controller;

import com.banking.app.service.CircuitBreakerService;
import com.banking.app.service.PaymentGatewayService;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "9. ⚡ Circuit Breaker", description = "Test circuit breaker with simulated failures")
@RequestMapping("/api/v1/circuit-breaker")
public class CircuitBreakerController {

    private final CircuitBreakerService circuitBreakerService;
    private final PaymentGatewayService paymentGatewayService;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Operation(summary = "Test payment with circuit breaker")
    @PostMapping("/payment")
    public ResponseEntity<Map<String, Object>> testPayment(@RequestBody Map<String, Object> request) {
        Long accountId = Long.valueOf(request.get("accountId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String description = request.getOrDefault("description", "Test payment").toString();

        Map<String, Object> result = circuitBreakerService.processPayment(accountId, amount, description);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Test loan approval with circuit breaker")
    @PostMapping("/loan/{loanId}")
    public ResponseEntity<Map<String, Object>> testLoanApproval(@PathVariable Long loanId) {
        Map<String, Object> result = circuitBreakerService.processLoanApproval(loanId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Test transfer with circuit breaker")
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> testTransfer(@RequestBody Map<String, Object> request) {
        Long fromAccount = Long.valueOf(request.get("fromAccountId").toString());
        Long toAccount = Long.valueOf(request.get("toAccountId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        Map<String, Object> result = circuitBreakerService.processExternalTransfer(
                fromAccount, toAccount, amount);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Simulate gateway DOWN")
    @PostMapping("/gateway/down")
    public ResponseEntity<Map<String, String>> gatewayDown() {
        paymentGatewayService.enableFailureMode();
        return ResponseEntity.ok(Map.of(
                "message", "Payment Gateway is now DOWN (failure mode enabled)",
                "status", "DOWN"
        ));
    }

    @Operation(summary = "Simulate gateway UP")
    @PostMapping("/gateway/up")
    public ResponseEntity<Map<String, String>> gatewayUp() {
        paymentGatewayService.disableFailureMode();
        return ResponseEntity.ok(Map.of(
                "message", "Payment Gateway has RECOVERED (failure mode disabled)",
                "status", "UP"
        ));
    }

    @Operation(summary = "Check gateway status")
    @GetMapping("/gateway/status")
    public ResponseEntity<Map<String, Object>> gatewayStatus() {
        return ResponseEntity.ok(Map.of(
                "gatewayUp", !paymentGatewayService.isFailureModeEnabled(),
                "failureMode", paymentGatewayService.isFailureModeEnabled()
        ));
    }

    @Operation(summary = "View all circuit breaker states")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> circuitBreakerStatus() {
        Map<String, Object> statuses = new HashMap<>();

        circuitBreakerRegistry.getAllCircuitBreakers().forEach(cb -> {
            Map<String, Object> details = new HashMap<>();
            details.put("state", cb.getState().toString());
            details.put("failureRate", cb.getMetrics().getFailureRate() + "%");
            details.put("totalCalls", cb.getMetrics().getNumberOfBufferedCalls());
            details.put("failedCalls", cb.getMetrics().getNumberOfFailedCalls());
            details.put("successCalls", cb.getMetrics().getNumberOfSuccessfulCalls());
            details.put("notPermittedCalls", cb.getMetrics().getNumberOfNotPermittedCalls());
            statuses.put(cb.getName(), details);
        });

        return ResponseEntity.ok(statuses);
    }
}
