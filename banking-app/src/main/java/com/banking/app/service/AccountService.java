package com.banking.app.service;

import com.banking.app.exception.InsufficientBalanceException;
import com.banking.app.model.Account;
import com.banking.app.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.banking.app.exception.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public Account createAccount(Account account){
        String accountNumber = "ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        account.setAccountNumber(accountNumber);

        if (account.getBalance() == null){
            account.setBalance(BigDecimal.ZERO);
        }

        Account savedAccount = accountRepository.save(account);
        log.info("Account created: {} for {}", savedAccount.getAccountNumber(), savedAccount.getAccountName());
        return savedAccount;
    }

    public Account getAccountById(Long id){
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " +id));
    }

    public List<Account> getAllAccounts(){
        return accountRepository.findAll();
    }

    @Transactional
    public Account deposit(Long accountId, BigDecimal amount){
        if (amount.compareTo(BigDecimal.ZERO) <=0){
            throw new IllegalArgumentException("Deposit Amount must be Positive");
        }

        Account account = getAccountById(accountId);
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);

        Account updated = accountRepository.save(account);
        log.info("Deposited {} to account {}. New Balance: {}", amount, account.getAccountNumber(), newBalance);
        return updated;
    }

    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount){
        if (amount.compareTo(BigDecimal.ZERO) <=0){
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        Account account = getAccountById(accountId);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance! Available: " + account.getBalance() + ", Requested: " + amount);
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);

        Account updated = accountRepository.save(account);
        log.info("Withdrawn {} from account {}. New balance: {}", amount, account.getAccountNumber(), newBalance);
        return updated;
    }

    @Transactional
    public String transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        withdraw(fromAccountId, amount);
        deposit(toAccountId, amount);

        log.info("Transferred {} from account ID {} to account ID {}", amount, fromAccountId, toAccountId);
        return "Successfully transferred " + amount + " from account " + fromAccountId + " to account " + toAccountId;
    }

    @Transactional
    public void deleteAccount(Long id){
        Account account = getAccountById(id);
        accountRepository.delete(account);
        log.info("Account deleted: {}", account.getAccountNumber());

    }
}
