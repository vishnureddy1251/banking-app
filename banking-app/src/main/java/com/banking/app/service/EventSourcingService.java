package com.banking.app.service;

import com.banking.app.model.AccountEvent;
import com.banking.app.repository.AccountEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
}
