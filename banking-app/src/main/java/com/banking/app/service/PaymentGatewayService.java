package com.banking.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
public class PaymentGatewayService {

    private boolean failureMode = false;

    private final Random random = new Random();

    public Map<String, Object> processPayment(Long accountId, BigDecimal amount, String description){

        log.info("Calling Payment Gateway for account {} - {}", accountId, amount);

        try{
            Thread.sleep(500);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }

        if (failureMode){
            log.error("Payment Gateway is DOWN! (failure mode ON)");
            throw new RuntimeException("Payment Gateway is unavailable - connection timeout");
        }

        if (random.nextInt(10) < 3){
            log.error("Payment Gateway random failure!");
            throw new RuntimeException("Payment Gateway error - service temporarily unavailable");
        }

        String transactionRef = "PG" + System.currentTimeMillis();
        log.info("Payment successful! Ref: {}", transactionRef);

        return Map.of(
                "status", "SUCCESS",
                "referenceNumber", transactionRef,
                "amount", amount.toString(),
                "message", "Payment processed successfully"
        );

    }

    public boolean isFailureModeEnabled(){
        return this.failureMode;
    }
}
