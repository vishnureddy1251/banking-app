package com.banking.app.service;

import com.banking.app.model.AccountEvent;
import com.banking.app.repository.AccountEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        Long sequenceNumber = accountEventRepository.getNextSequenceNumber(accountId);
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

        AccountEvent saved = accountEventRepository.save(event);
        log.info("EVENT PUBLISHED: [{}] Account {} - ${} - Balance: {}",
                eventType, accountId, amount, balanceAfter);
        return saved;
    }

    public List<AccountEvent> getEventHistory(Long accountId) {
        return accountEventRepository.findByAccountIdOrderBySequenceNumberAsc(accountId);
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


        AccountEvent lastEvent = events.get(events.size() - 1);

        Map<String, Object> state = new LinkedHashMap<>();
        state.put("accountId", accountId);
        state.put("calculatedBalance", calculatedBalance);
        state.put("storedBalance", lastEvent.getBalanceAfter());
        state.put("balancesMatch", calculatedBalance.compareTo(lastEvent.getBalanceAfter()) == 0);
        state.put("totalDeposits", totalDeposits);
        state.put("totalWithdrawals", totalWithdrawals);
        state.put("totalTransfersIn", totalTransfersIn);
        state.put("totalTransfersOut", totalTransfersOut);
        state.put("totalEvents", eventCount);
        state.put("firstEvent", events.get(0).getEventTimestamp().toString());
        state.put("lastEvent", lastEvent.getEventTimestamp().toString());

        return state;
    }

    public Map<String, Object> getStateAtEvent(Long accountId, Long sequenceNumber) {
        List<AccountEvent> events = getEventHistory(accountId);

        BigDecimal balance = BigDecimal.ZERO;
        AccountEvent targetEvent = null;

        for (AccountEvent event : events) {
            if (event.getSequenceNumber() > sequenceNumber) break;

            targetEvent = event;
            switch (event.getEventType()) {
                case "ACCOUNT_CREATED":
                    balance = event.getBalanceAfter();
                    break;
                case "DEPOSITED", "TRANSFERRED_IN":
                    balance = balance.add(event.getAmount());
                    break;
                case "WITHDRAWN", "TRANSFERRED_OUT":
                    balance = balance.subtract(event.getAmount());
                    break;
            }
        }

        if (targetEvent == null) {
            return Map.of("error", "No events found up to sequence " + sequenceNumber);
        }

        return Map.of(
                "accountId", accountId,
                "atSequence", sequenceNumber,
                "balanceAtThatPoint", balance,
                "eventType", targetEvent.getEventType(),
                "timestamp", targetEvent.getEventTimestamp().toString()
        );
    }

    public List<AccountEvent> getEventsByType(Long accountId, String eventType) {
        return accountEventRepository.findByAccountIdAndEventTypeOrderBySequenceNumberAsc(
                accountId, eventType.toUpperCase());
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "SYSTEM";
    }

}
