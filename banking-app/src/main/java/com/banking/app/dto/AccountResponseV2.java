package com.banking.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseV2 {

    private Long id;
    private String accountName;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String formattedBalance;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String apiVersion; 

}
