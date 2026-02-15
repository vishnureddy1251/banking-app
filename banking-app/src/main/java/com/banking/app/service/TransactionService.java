package com.banking.app.service;

import com.banking.app.model.Transaction;
import com.banking.app.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Transaction logTransaction(Long accountId, String type, BigDecimal amount,
                                      BigDecimal balanceAfter, Long relatedAccountId,
                                      String description) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setTransactionType(type);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setRelatedAccountId(relatedAccountId);
        transaction.setDescription(description);

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction logged: {} - {} - ${}", type, accountId, amount);
        return saved;
    }

    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId);
    }

    public List<Transaction> getTransactionsByType(Long accountId, String type) {
        return transactionRepository.findByAccountIdAndTransactionType(accountId, type);
    }
}
