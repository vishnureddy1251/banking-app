package com.banking.app.controller;

import com.banking.app.model.Loan;
import com.banking.app.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "6. 🏦 Loans", description = "Apply, approve, reject, and repay loans")
@RequestMapping("/api/v1/loans")
public class LoanController {

    private final LoanService loanService;

    @Operation(summary = "Apply for a loan")
    @PostMapping
    public ResponseEntity<Loan> applyForLoan(@Valid @RequestBody Loan loan) {
        Loan created = loanService.applyForLoan(loan);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all loans")
    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @Operation(summary = "Get loan by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @Operation(summary = "Get loans for an account")
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Loan>> getLoansByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(loanService.getLoansByAccountId(accountId));
    }

    @Operation(summary = "Filter loans by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Loan>> getLoansByStatus(@PathVariable String status) {
        return ResponseEntity.ok(loanService.getLoansByStatus(status));
    }

    @Operation(summary = "Approve loan (ADMIN only)")
    @PutMapping("/{id}/approve")
    public ResponseEntity<Loan> approveLoan(@PathVariable Long id) {
        log.info("REST: Loan approval request for loan ID {}", id);
        return ResponseEntity.ok(loanService.approveLoan(id));
    }

    @Operation(summary = "Reject loan (ADMIN only)")
    @PutMapping("/{id}/reject")
    public ResponseEntity<Loan> rejectLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.rejectLoan(id));
    }

    @Operation(summary = "Repay loan")
    @PutMapping("/{id}/repay")
    public ResponseEntity<Loan> repayLoan(@PathVariable Long id, @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        return ResponseEntity.ok(loanService.repayLoan(id, amount));
    }
}
