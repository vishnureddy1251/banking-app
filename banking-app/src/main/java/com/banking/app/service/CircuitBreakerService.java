package com.banking.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CircuitBreakerService {

    private final PaymentGatewayService paymentGatewayService;

    public Map<String, Object> processPayment(Long accountId, BigDecimal amount, String description){
        log.info("Processing payment through circuit breaker...");
        return PaymentGatewayService.processPayment(accountId, amount, description);
    }
}
