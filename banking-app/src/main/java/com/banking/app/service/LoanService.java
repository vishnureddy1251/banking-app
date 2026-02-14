package com.banking.app.service;

import com.banking.app.model.Loan;
import com.banking.app.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

    private final LoanRepository loanRepository;
    private final AccountService accountService;

    @Transactional
    public Loan applyForLoan(Loan loan) {
        accountService.getAccountById(loan.getAccountId());

        loan.setStatus("PENDING");
        loan.setAmountPaid(BigDecimal.ZERO);
        loan.setRemainingBalance(loan.getLoanAmount());

        Loan saved = loanRepository.save(loan);
        log.info("Loan application submitted: ID {} for account {}", saved.getId(), saved.getAccountId());
        return saved;
    }
}
