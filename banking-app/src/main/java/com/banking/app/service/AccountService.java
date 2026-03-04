package com.banking.app.service;

import com.banking.app.exception.AccountNotFoundException;
import com.banking.app.exception.InsufficientBalanceException;
import com.banking.app.model.Account;
import com.banking.app.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final WebSocketNotificationService wsNotificationService;

    @Transactional
    @CacheEvict(value = {"accounts", "allAccounts"}, allEntries = true)
    public Account createAccount(Account account) {
        String accountNumber = "ACC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        account.setAccountNumber(accountNumber);
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        Account savedAccount = accountRepository.save(account);
        log.info("Account created: {} for {}", savedAccount.getAccountNumber(), savedAccount.getAccountName());
        return savedAccount;
    }

    @Cacheable(value = "accounts", key = "#id")
    public Account getAccountById(Long id) {
        log.info("CACHE MISS - Fetching account {} from database", id);
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));
    }

    @Cacheable(value = "allAccounts")
    public List<Account> getAllAccounts() {
        log.info("CACHE MISS - Fetching all accounts from database");
        return accountRepository.findAll();
    }

    @Transactional
    @CacheEvict(value = {"accounts", "allAccounts"}, allEntries = true)
    public Account deposit(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        Account updated = accountRepository.save(account);

        transactionService.logTransaction(accountId, "DEPOSIT", amount, newBalance, null, "Cash deposit");

        log.info("Deposited {} to account {}. New balance: {}", amount, account.getAccountNumber(), newBalance);

        wsNotificationService.notifyDeposit(accountId, amount, newBalance);
        if (newBalance.compareTo(new BigDecimal("500")) < 0) {
            wsNotificationService.notifyLowBalance(accountId, newBalance);
        }
        return updated;
    }

    @Transactional
    @CacheEvict(value = {"accounts", "allAccounts"}, allEntries = true)
    public Account withdraw(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance! Available: " + account.getBalance() + ", Requested: " + amount);
        }
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        Account updated = accountRepository.save(account);

        transactionService.logTransaction(accountId, "WITHDRAW", amount, newBalance, null, "Cash withdrawal");

        log.info("Withdrawn {} from account {}. New balance: {}", amount, account.getAccountNumber(), newBalance);

        wsNotificationService.notifyWithdrawal(accountId, amount, newBalance);
        if (newBalance.compareTo(new BigDecimal("500")) < 0) {
            wsNotificationService.notifyLowBalance(accountId, newBalance);
        }
        return updated;
    }

    @Transactional
    @CacheEvict(value = {"accounts", "allAccounts"}, allEntries = true)
    public String transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + fromAccountId));
        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + toAccountId));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance! Available: " + fromAccount.getBalance() + ", Requested: " + amount);
        }

        BigDecimal fromNewBalance = fromAccount.getBalance().subtract(amount);
        fromAccount.setBalance(fromNewBalance);
        accountRepository.save(fromAccount);

        BigDecimal toNewBalance = toAccount.getBalance().add(amount);
        toAccount.setBalance(toNewBalance);
        accountRepository.save(toAccount);

        transactionService.logTransaction(fromAccountId, "TRANSFER_OUT", amount, fromNewBalance,
                toAccountId, "Transfer to account #" + toAccountId);
        transactionService.logTransaction(toAccountId, "TRANSFER_IN", amount, toNewBalance,
                fromAccountId, "Transfer from account #" + fromAccountId);

        log.info("Transferred {} from account ID {} to account ID {}", amount, fromAccountId, toAccountId);

        wsNotificationService.notifyTransferSent(fromAccountId, toAccountId, amount, fromNewBalance);
        wsNotificationService.notifyTransferReceived(toAccountId, fromAccountId, amount, toNewBalance);
        if (fromNewBalance.compareTo(new BigDecimal("500")) < 0) {
            wsNotificationService.notifyLowBalance(fromAccountId, fromNewBalance);
        }
        return "Successfully transferred " + amount + " from account " + fromAccountId + " to account " + toAccountId;
    }

    @Transactional
    @CacheEvict(value = {"accounts", "allAccounts"}, allEntries = true)
    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));
        accountRepository.delete(account);
        log.info("Account deleted: {}", account.getAccountNumber());
    }
}