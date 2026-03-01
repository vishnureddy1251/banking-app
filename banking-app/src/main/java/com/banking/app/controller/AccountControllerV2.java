package com.banking.app.controller;

import com.banking.app.dto.AccountResponseV2;
import com.banking.app.dto.TransferRequestV2;
import com.banking.app.model.Account;
import com.banking.app.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/accounts")
@RequiredArgsConstructor
public class AccountControllerV2 {

    private final AccountService accountService;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    @GetMapping
    public ResponseEntity<List<AccountResponseV2>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        List<AccountResponseV2> response = accounts.stream()
                .map(this::toV2Response)
                .toList();
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<AccountResponseV2> getAccountById(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(toV2Response(account));
    }

    @PostMapping
    public ResponseEntity<AccountResponseV2> createAccount(@Valid @RequestBody Account account) {
        Account created = accountService.createAccount(account);
        return ResponseEntity.ok(toV2Response(created));
    }

    @PutMapping("/{id}/deposit")
    public ResponseEntity<AccountResponseV2> deposit(
            @PathVariable Long id, @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        Account updated = accountService.deposit(id, amount);
        return ResponseEntity.ok(toV2Response(updated));
    }

    private AccountResponseV2 toV2Response(Account account) {
        return AccountResponseV2.builder()
                .id(account.getId())
                .accountName(account.getAccountName())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .formattedBalance(currencyFormatter.format(account.getBalance()))
                .status(determineStatus(account))
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .apiVersion("v2")
                .build();
    }

    private String determineStatus(Account account) {
        if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) return "OVERDRAWN";
        if (account.getBalance().compareTo(BigDecimal.ZERO) == 0) return "EMPTY";
        return "ACTIVE";
    }
}
