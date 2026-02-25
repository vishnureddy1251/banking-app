package com.banking.app.service;

import com.banking.app.model.AccountEvent;
import com.banking.app.repository.AccountEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventSourcingService {

    private final AccountEventRepository accountEventRepository;

    @Transactional
    public AccountEvent publishEvent(Long accountId, String eventType, BigDecimal amount,
                                     BigDecimal balanceAfter, Long relatedAccountId,
                                     String metadata) {

        Long sequenceNumber = eventRepository.getNextSequenceNumber(accountId);
        String triggeredBy = getCurrentUser();

        AccountEvent event = AccountEvent.builder()
                .accountId(accountId)
                .eventType(eventType)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .triggeredBy(triggeredBy)
                .relatedAccountId(relatedAccountId)
                .metadata(metadata)
                .sequenceNumber(sequenceNumber)
                .build();

        AccountEvent saved = eventRepository.save(event);
        log.info("EVENT PUBLISHED: [{}] Account {} - ${} - Balance: {}",
                eventType, accountId, amount, balanceAfter);
        return saved;
    }

    public List<AccountEvent> getEventHistory(Long accountId) {
        return eventRepository.findByAccountIdOrderBySequenceNumberAsc(accountId);
    }

    public Map<String, Object> rebuildAccountState(Long accountId) {
        List<AccountEvent> events = getEventHistory(accountId);

        if (events.isEmpty()) {
            return Map.of("error", "No events found for account " + accountId);
        }

        BigDecimal calculatedBalance = BigDecimal.ZERO;
        BigDecimal totalDeposits = BigDecimal.ZERO;
        BigDecimal totalWithdrawals = BigDecimal.ZERO;
        BigDecimal totalTransfersIn = BigDecimal.ZERO;
        BigDecimal totalTransfersOut = BigDecimal.ZERO;
        int eventCount = 0;

        for (AccountEvent event : events) {
            eventCount++;
            switch (event.getEventType()) {
                case "ACCOUNT_CREATED":
                    calculatedBalance = event.getBalanceAfter();
                    break;
                case "DEPOSITED":
                    calculatedBalance = calculatedBalance.add(event.getAmount());
                    totalDeposits = totalDeposits.add(event.getAmount());
                    break;
                case "WITHDRAWN":
                    calculatedBalance = calculatedBalance.subtract(event.getAmount());
                    totalWithdrawals = totalWithdrawals.add(event.getAmount());
                    break;
                case "TRANSFERRED_IN":
                    calculatedBalance = calculatedBalance.add(event.getAmount());
                    totalTransfersIn = totalTransfersIn.add(event.getAmount());
                    break;
                case "TRANSFERRED_OUT":
                    calculatedBalance = calculatedBalance.subtract(event.getAmount());
                    totalTransfersOut = totalTransfersOut.add(event.getAmount());
                    break;
            }
        }
    }

}
