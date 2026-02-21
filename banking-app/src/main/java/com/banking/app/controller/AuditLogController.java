package com.banking.app.controller;

import com.banking.app.model.AuditLog;
import com.banking.app.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLog>> getRecentLogs() {
        return ResponseEntity.ok(auditLogService.getRecentLogs());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<AuditLog>> getLogsByUser(@PathVariable String username) {
        return ResponseEntity.ok(auditLogService.getLogsByUser(username));
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditLog>> getLogsByAction(@PathVariable String action) {
        return ResponseEntity.ok(auditLogService.getLogsByAction(action.toUpperCase()));
    }

    @GetMapping("/entity/{entityType}")
    public ResponseEntity<List<AuditLog>> getLogsByEntity(@PathVariable String entityType) {
        return ResponseEntity.ok(auditLogService.getLogsByEntity(entityType.toUpperCase()));
    }

    @GetMapping("/date")
    public ResponseEntity<List<AuditLog>> getLogsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(auditLogService.getLogsByDateRange(start, end));
    }
}
