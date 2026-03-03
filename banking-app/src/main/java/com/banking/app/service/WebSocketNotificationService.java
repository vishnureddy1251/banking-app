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

    public void notifyWithdrawal(Long accountId, BigDecimal amount, BigDecimal newBalance) {
        Map<String, Object> payload = Map.of(
                "type", "WITHDRAWAL",
                "title", "Withdrawal Made",
                "message", "You withdrew $" + amount + " from your account",
                "amount", amount.toString(),
                "newBalance", newBalance.toString(),
                "accountId", accountId,
                "timestamp", LocalDateTime.now().toString()
        );

        sendToAccount(accountId, payload);
        log.info("WebSocket: Withdrawal notification sent to account {}", accountId);
    }

    public void notifyTransferSent(Long fromAccountId, Long toAccountId,
                                   BigDecimal amount, BigDecimal newBalance) {
        Map<String, Object> payload = Map.of(
                "type", "TRANSFER_SENT",
                "title", "Transfer Sent",
                "message", "You sent $" + amount + " to account #" + toAccountId,
                "amount", amount.toString(),
                "newBalance", newBalance.toString(),
                "toAccountId", toAccountId,
                "accountId", fromAccountId,
                "timestamp", LocalDateTime.now().toString()
        );

        sendToAccount(fromAccountId, payload);
        log.info("WebSocket: Transfer sent notification to account {}", fromAccountId);
    }

    public void notifyTransferReceived(Long toAccountId, Long fromAccountId,
                                       BigDecimal amount, BigDecimal newBalance) {
        Map<String, Object> payload = Map.of(
                "type", "TRANSFER_RECEIVED",
                "title", "Transfer Received",
                "message", "You received $" + amount + " from account #" + fromAccountId,
                "amount", amount.toString(),
                "newBalance", newBalance.toString(),
                "fromAccountId", fromAccountId,
                "accountId", toAccountId,
                "timestamp", LocalDateTime.now().toString()
        );

        sendToAccount(toAccountId, payload);
        log.info("WebSocket: Transfer received notification to account {}", toAccountId);
    }
}
