package com.banking.app.controller;

import com.banking.app.service.TimeoutService;
import com.banking.app.service.WriteBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@Tag(name = "15. ⏱️ System Patterns", description = "Test timeouts and write batching")
@RequestMapping("/api/v1/system")
public class SystemPatternsController {

    private final TimeoutService timeoutService;
    private final WriteBatchService writeBatchService;

    @Operation(summary = "Test payment with 3s timeout")
    @PostMapping("/timeout/payment")
    public CompletableFuture<Map<String, Object>> testPaymentTimeout(
            @RequestBody Map<String, Object> request) {
        Long accountId = Long.valueOf(request.get("accountId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        return timeoutService.processPaymentWithTimeout(accountId, amount);
    }

    @Operation(summary = "Queue transactions for batch writing")
    @PostMapping("/batch/queue")
    public ResponseEntity<Map<String, Object>> queueTransactions(
            @RequestBody Map<String, Object> request) {
        Long accountId = Long.valueOf(request.get("accountId").toString());
        int count = Integer.parseInt(request.getOrDefault("count", "10").toString());

        for (int i = 1; i <= count; i++) {
            writeBatchService.queueTransaction(
                    accountId,
                    "BATCH_TEST",
                    new BigDecimal(i * 100),
                    new BigDecimal("5000"),
                    null,
                    "Batch transaction #" + i
            );
        }

        return ResponseEntity.ok(Map.of(
                "message", count + " transactions queued. Will be flushed in next 5-second cycle.",
                "stats", writeBatchService.getStats()
        ));
    }

    @Operation(summary = "Force flush queued transactions")
    @PostMapping("/batch/flush")
    public ResponseEntity<Map<String, Object>> forceFlush() {
        int flushed = writeBatchService.forceFlush();
        return ResponseEntity.ok(Map.of(
                "message", flushed + " transactions flushed to database",
                "stats", writeBatchService.getStats()
        ));
    }

    @Operation(summary = "View batch processing stats")
    @GetMapping("/batch/stats")
    public ResponseEntity<Map<String, Object>> getBatchStats() {
        return ResponseEntity.ok(writeBatchService.getStats());
    }
}
