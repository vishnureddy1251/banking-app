package com.banking.app.controller;

import com.banking.app.model.Transaction;
import com.banking.app.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{accountId}")
    public ResponseEntity<List<Transaction>> getTransaction(
            @PathVariable Long accountId,
            @RequestParam(required = false) String type){

        List<Transaction> transactions;
        if (type != null && !type.isEmpty()) {
            transactions = transactionService.getTransactionsByType(accountId, type.toUpperCase());
        } else {
            transactions = transactionService.getTransactionsByAccountId(accountId);
        }
        return ResponseEntity.ok(transactions);

    }
}
