package com.banking.app.controller;

import com.banking.app.service.TimeoutService;
import com.banking.app.service.WriteBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/system")
public class SystemPatternsController {

    private final TimeoutService timeoutService;
    private final WriteBatchService writeBatchService;

    @PostMapping("/timeout/payment")
    public CompletableFuture<Map<String, Object>> testPaymentTimeout(
            @RequestBody Map<String, Object> request) {
        Long accountId = Long.valueOf(request.get("accountId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        return timeoutService.processPaymentWithTimeout(accountId, amount);
    }

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
}
