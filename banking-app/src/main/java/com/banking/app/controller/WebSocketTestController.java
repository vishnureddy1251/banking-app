package com.banking.app.controller;

import com.banking.app.service.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ws-test")
@RequiredArgsConstructor
public class WebSocketTestController {

    private final WebSocketNotificationService wsService;

    @PostMapping("/deposit")
    public ResponseEntity<Map<String, String>> testDeposit(@RequestBody Map<String, Object> request) {
        Long accountId = Long.valueOf(request.get("accountId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        wsService.notifyDeposit(accountId, amount, new BigDecimal("10000"));
        return ResponseEntity.ok(Map.of("message", "Deposit notification sent to account " + accountId));
    }
}
