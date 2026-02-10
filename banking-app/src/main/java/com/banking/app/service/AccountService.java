package com.banking.app.service;

import com.banking.app.model.Account;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

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
}
