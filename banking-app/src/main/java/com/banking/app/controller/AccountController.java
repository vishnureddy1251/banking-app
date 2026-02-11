package com.banking.app.controller;

import com.banking.app.model.Account;
import com.banking.app.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@Valid @RequestBody Account account){
        Account created = AccountService.createAccount(account);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{id}/deposit")
    public ResponseEntity<Account> deposit(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> request) {

        BigDecimal amount = request.get("amount");
        Account updated = accountService.deposit(id, amount);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/withdraw")
    public ResponseEntity<Account> withdraw(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> request) {

        BigDecimal amount = request.get("amount");
        Account updated = accountService.withdraw(id, amount);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transfer(
            @RequestBody Map<String, Object> request) {

        Long fromId = Long.valueOf(request.get("fromAccountId").toString());
        Long toId = Long.valueOf(request.get("toAccountId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        String result = accountService.transfer(fromId, toId, amount);
        return ResponseEntity.ok(Map.of("message", result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
    }
}
