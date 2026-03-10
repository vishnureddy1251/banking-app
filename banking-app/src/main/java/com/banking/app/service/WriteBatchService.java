package com.banking.app.service;

import com.banking.app.model.Transaction;
import com.banking.app.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class WriteBatchService {

    private final TransactionRepository transactionRepository;

    private final ConcurrentLinkedQueue<Transaction> transactionQueue = new ConcurrentLinkedQueue<>();
    private final AtomicInteger totalBatched = new AtomicInteger(0);
    private final AtomicInteger totalFlushed = new AtomicInteger(0);

    public void queueTransaction(Long accountId, String type, BigDecimal amount,
                                 BigDecimal balanceAfter, Long relatedAccountId,
                                 String description) {

        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setTransactionType(type);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setRelatedAccountId(relatedAccountId);
        transaction.setDescription(description);
        transaction.setTransactionDate(LocalDateTime.now());

        transactionQueue.add(transaction);
        totalBatched.incrementAndGet();

        log.debug("Transaction queued for account {}. Queue size: {}",
                accountId, transactionQueue.size());
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void flushTransactions() {
        if (transactionQueue.isEmpty()) return;

        List<Transaction> batch = new ArrayList<>();
        Transaction transaction;

        while ((transaction = transactionQueue.poll()) != null) {
            batch.add(transaction);
        }

        if (!batch.isEmpty()) {
            transactionRepository.saveAll(batch);
            totalFlushed.addAndGet(batch.size());
            log.info("BATCH FLUSH: Saved {} transactions in one batch. Total flushed: {}",
                    batch.size(), totalFlushed.get());
        }
    }

    @Transactional
    public int forceFlush() {
        List<Transaction> batch = new ArrayList<>();
        Transaction transaction;
        while ((transaction = transactionQueue.poll()) != null) {
            batch.add(transaction);
        }
        if (!batch.isEmpty()) {
            transactionRepository.saveAll(batch);
            totalFlushed.addAndGet(batch.size());
            log.info("FORCE FLUSH: Saved {} transactions", batch.size());
        }
        return batch.size();
    }

    public Map<String, Object> getStats() {
        return Map.of(
                "queueSize", transactionQueue.size(),
                "totalQueued", totalBatched.get(),
                "totalFlushed", totalFlushed.get(),
                "pending", totalBatched.get() - totalFlushed.get(),
                "batchSize", 20,
                "flushIntervalSeconds", 5
        );
    }
}
