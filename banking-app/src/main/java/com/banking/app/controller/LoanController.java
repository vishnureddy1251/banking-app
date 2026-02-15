package com.banking.app.controller;

import com.banking.app.model.Loan;
import com.banking.app.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<Loan> applyForLoan(@Valid @RequestBody Loan loan) {
        Loan created = loanService.applyForLoan(loan);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
