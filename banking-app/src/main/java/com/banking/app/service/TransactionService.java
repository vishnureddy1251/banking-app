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
                                      String description){
        Transaction transaction = Transaction.builder()
                .accountId(accountId)
                .transactionType(type)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .relatedAccountId(relatedAccountId)
                .description(description)
                .build();

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
