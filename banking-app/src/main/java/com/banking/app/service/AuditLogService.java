package com.banking.app.service;

import com.banking.app.model.AuditLog;
import com.banking.app.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLog logAction(String performedBy, String action, String entityType,
                              Long entityId, String description, String httpMethod,
                              String endpoint, String ipAddress, Integer statusCode) {

        AuditLog auditLog = AuditLog.builder()
                .performedBy(performedBy != null ? performedBy : "ANONYMOUS")
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .description(description)
                .httpMethod(httpMethod)
                .endpoint(endpoint)
                .ipAddress(ipAddress)
                .statusCode(statusCode)
                .build();

        AuditLog saved = auditLogRepository.save(auditLog);
        log.info("AUDIT: [{}] {} - {} - {}", action, performedBy, entityType, description);
        return saved;
    }

    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop50ByOrderByTimestampDesc();
    }

    public List<AuditLog> getLogsByUser(String username) {
        return auditLogRepository.findByPerformedByOrderByTimestampDesc(username);
    }

    public List<AuditLog> getLogsByAction(String action) {
        return auditLogRepository.findByActionOrderByTimestampDesc(action);
    }

    public List<AuditLog> getLogsByEntity(String entityType) {
        return auditLogRepository.findByEntityTypeOrderByTimestampDesc(entityType);
    }

    public List<AuditLog> getLogsByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        return auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);
    }
}
