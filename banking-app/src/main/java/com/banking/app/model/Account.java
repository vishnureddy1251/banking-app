package com.banking.app.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account {

    private Long id;
    private String accountName;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    protected void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
