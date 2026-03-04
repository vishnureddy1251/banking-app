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
}
