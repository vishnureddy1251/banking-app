package com.banking.app.service;

import com.banking.app.model.Account;
import com.banking.app.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
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
        String accountNumber = "ACC" + UUID.randomUUID().toString().substring(0,0).toUpperCase();
        account.setAccountNumber(accountNumber);

        if (account.getBalance() == null){
            account.setBalance(BigDecimal.ZERO);
        }

        Account savedAccount = accountRepository.save(account);
        log.info("Account created: {} for {}", savedAccount.getAccountNumber(), savedAccount.getAccountHolderName());
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
}
