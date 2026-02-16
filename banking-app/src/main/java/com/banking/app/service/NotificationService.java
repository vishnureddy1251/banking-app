package com.banking.app.service;

import com.banking.app.model.Notification;
import com.banking.app.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification sendNotification(Long accountId, String channel, String category,
                                         String subject, String message) {
        Notification notification = Notification.builder()
                .accountId(accountId)
                .channel(channel)
                .category(category)
                .subject(subject)
                .message(message)
                .status("SENT")
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Notification sent [{}]: {} - {}", channel, subject, accountId);
        return saved;
    }

    public void sendTransactionAlert(Long accountId, String type, BigDecimal amount, BigDecimal balance) {
        String subject = type + " Alert";
        String message = String.format("Your account has been %s with $%s. Current balance: $%s",
                type.toLowerCase() + "ed", amount, balance);
        sendNotification(accountId, "EMAIL", "TRANSACTION", subject, message);
    }

}
