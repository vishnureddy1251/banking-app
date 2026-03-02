package com.banking.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyDeposit(Long accountId, BigDecimal amount, BigDecimal newBalance) {
        Map<String, Object> payload = Map.of(
                "type", "DEPOSIT",
                "title", "Deposit Received",
                "message", "You received a deposit of $" + amount,
                "amount", amount.toString(),
                "newBalance", newBalance.toString(),
                "accountId", accountId,
                "timestamp", LocalDateTime.now().toString()
        );

        sendToAccount(accountId, payload);
        log.info("WebSocket: Deposit notification sent to account {}", accountId);
    }
}
