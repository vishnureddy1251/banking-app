package com.banking.app.controller;

import com.banking.app.model.Notification;
import com.banking.app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "8. 🔔 Notifications", description = "View and manage notifications")
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{accountId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long accountId) {
        return ResponseEntity.ok(notificationService.getNotifications(accountId));
    }

    @GetMapping("/{accountId}/unread")
    public ResponseEntity<List<Notification>> getUnread(@PathVariable Long accountId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(accountId));
    }

    @GetMapping("/{accountId}/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long accountId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(accountId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @PutMapping("/{accountId}/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(@PathVariable Long accountId) {
        notificationService.markAllAsRead(accountId);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }


}
