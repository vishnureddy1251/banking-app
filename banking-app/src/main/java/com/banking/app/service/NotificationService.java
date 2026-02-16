package com.banking.app.service;

import com.banking.app.exception.AccountNotFoundException;
import com.banking.app.model.Notification;
import com.banking.app.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    public List<Notification> getNotifications(Long accountId) {
        return notificationRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    public List<Notification> getUnreadNotifications(Long accountId) {
        return notificationRepository.findByAccountIdAndIsReadFalseOrderByCreatedAtDesc(accountId);
    }

    public Map<String, Long> getUnreadCount(Long accountId) {
        long count = notificationRepository.countByAccountIdAndIsReadFalse(accountId);
        return Map.of("unreadCount", count);
    }

    @Transactional
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AccountNotFoundException("Notification not found: " + notificationId));
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long accountId) {
        List<Notification> unread = notificationRepository
                .findByAccountIdAndIsReadFalseOrderByCreatedAtDesc(accountId);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
        log.info("All notifications marked as read for account {}", accountId);
    }
}
