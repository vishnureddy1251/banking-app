package com.banking.app.service;

import com.banking.app.exception.AccountNotFoundException;
import com.banking.app.model.Loan;
import com.banking.app.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Transactional
    public Loan approveLoan(Long loanId) {
        Loan loan = getLoanById(loanId);

        if (!"PENDING".equals(loan.getStatus())) {
            throw new IllegalArgumentException("Loan is not in PENDING status. Current: " + loan.getStatus());
        }

        loan.setStatus("ACTIVE");
        loan.setApprovedDate(LocalDateTime.now());
        Loan saved = loanRepository.save(loan);

        accountService.deposit(loan.getAccountId(), loan.getLoanAmount());

        log.info("Loan approved: ID {} - ${} deposited to account {}", loanId, loan.getLoanAmount(), loan.getAccountId());
        return saved;
    }

    @Transactional
    public Loan rejectLoan(Long loanId) {
        Loan loan = getLoanById(loanId);

        if (!"PENDING".equals(loan.getStatus())) {
            throw new IllegalArgumentException("Loan is not in PENDING status. Current: " + loan.getStatus());
        }

        loan.setStatus("REJECTED");
        Loan saved = loanRepository.save(loan);
        log.info("Loan rejected: ID {}", loanId);
        return saved;
    }

    public Loan getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Loan not found with ID: " + id));
    }
}
