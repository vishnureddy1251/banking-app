package com.banking.app.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banking.app.model.AccountEvent;
import com.banking.app.service.EventSourcingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventSourcingController {

    private final EventSourcingService eventSourcingService;

        @PostMapping("/publish")
    public ResponseEntity<AccountEvent> publishEvent(@RequestBody Map<String, Object> request) {
        Long accountId = Long.valueOf(request.get("accountId").toString());
        String eventType = request.get("eventType").toString();
        BigDecimal amount = new BigDecimal(request.getOrDefault("amount", "0").toString());
        BigDecimal balanceAfter = new BigDecimal(request.get("balanceAfter").toString());
        Long relatedAccountId = request.get("relatedAccountId") != null ?
                Long.valueOf(request.get("relatedAccountId").toString()) : null;
        String metadata = request.getOrDefault("metadata", "").toString();

        AccountEvent event = eventSourcingService.publishEvent(
                accountId, eventType, amount, balanceAfter, relatedAccountId, metadata);
        return ResponseEntity.ok(event);
    }

}
