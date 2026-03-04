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
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

    private final LoanRepository loanRepository;
    private final AccountService accountService;
    private final WebSocketNotificationService wsNotificationService;

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
        wsNotificationService.notifyLoanApproved(loan.getAccountId(), loanId, loan.getLoanAmount());
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
        wsNotificationService.notifyLoanRejected(loan.getAccountId(), loanId);
        return saved;
    }

    @Transactional
    public Loan repayLoan(Long loanId, BigDecimal amount) {
        Loan loan = getLoanById(loanId);

        if (!"ACTIVE".equals(loan.getStatus())) {
            throw new IllegalArgumentException("Loan is not ACTIVE. Current: " + loan.getStatus());
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Repayment amount must be positive");
        }

        if (amount.compareTo(loan.getRemainingBalance()) > 0) {
            throw new IllegalArgumentException(
                    "Payment exceeds remaining balance. Remaining: $" + loan.getRemainingBalance());
        }

        accountService.withdraw(loan.getAccountId(), amount);

        loan.setAmountPaid(loan.getAmountPaid().add(amount));
        loan.setRemainingBalance(loan.getRemainingBalance().subtract(amount));

        if (loan.getRemainingBalance().compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus("CLOSED");
            log.info("Loan fully repaid and CLOSED: ID {}", loanId);
        }

        Loan saved = loanRepository.save(loan);
        log.info("Loan repayment: ID {} - ${} paid. Remaining: ${}", loanId, amount, saved.getRemainingBalance());
        return saved;
    }

    public Loan getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Loan not found with ID: " + id));
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public List<Loan> getLoansByAccountId(Long accountId) {
        return loanRepository.findByAccountId(accountId);
    }

    public List<Loan> getLoansByStatus(String status) {
        return loanRepository.findByStatus(status.toUpperCase());
    }
}
