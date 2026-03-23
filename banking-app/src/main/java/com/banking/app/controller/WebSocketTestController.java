package com.banking.app.controller;

import com.banking.app.service.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "16. 🔌 WebSocket Test", description = "Trigger real-time WebSocket notifications")
@RequestMapping("/api/v1/ws-test")
public class WebSocketTestController {

    private final WebSocketNotificationService wsService;

    @PostMapping("/deposit")
    public ResponseEntity<Map<String, String>> testDeposit(@RequestBody Map<String, Object> request) {
        Long accountId = Long.valueOf(request.get("accountId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        wsService.notifyDeposit(accountId, amount, new BigDecimal("10000"));
        return ResponseEntity.ok(Map.of("message", "Deposit notification sent to account " + accountId));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> testTransfer(@RequestBody Map<String, Object> request) {
        Long fromId = Long.valueOf(request.get("fromAccountId").toString());
        Long toId = Long.valueOf(request.get("toAccountId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        wsService.notifyTransferSent(fromId, toId, amount, new BigDecimal("5000"));
        wsService.notifyTransferReceived(toId, fromId, amount, new BigDecimal("8000"));
        return ResponseEntity.ok(Map.of("message", "Transfer notifications sent"));
    }

    @PostMapping("/loan-approved")
    public ResponseEntity<Map<String, String>> testLoanApproved(@RequestBody Map<String, Object> request) {
        Long accountId = Long.valueOf(request.get("accountId").toString());
        Long loanId = Long.valueOf(request.get("loanId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        wsService.notifyLoanApproved(accountId, loanId, amount);
        return ResponseEntity.ok(Map.of("message", "Loan approved notification sent"));
    }

    @PostMapping("/low-balance")
    public ResponseEntity<Map<String, String>> testLowBalance(@RequestBody Map<String, Object> request) {
        Long accountId = Long.valueOf(request.get("accountId").toString());
        BigDecimal balance = new BigDecimal(request.get("balance").toString());

        wsService.notifyLowBalance(accountId, balance);
        return ResponseEntity.ok(Map.of("message", "Low balance alert sent to account " + accountId));
    }

    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, String>> testBroadcast(@RequestBody Map<String, Object> request) {
        String title = request.get("title").toString();
        String message = request.get("message").toString();

        wsService.broadcastSystemMessage(title, message);
        return ResponseEntity.ok(Map.of("message", "System broadcast sent to all users"));
    }
}
