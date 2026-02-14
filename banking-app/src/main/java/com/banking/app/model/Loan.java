package com.banking.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    private Long id;
    private Long accountId;
    private String loanType;
    private BigDecimal loanAmount;
    private BigDecimal amountPaid;
    private BigDecimal remainingBalance;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private String status = "PENDING";
    private LocalDateTime appliedDate;
    private LocalDateTime approvedDate;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate(){
        this.appliedDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.remainingBalance = this.loanAmount;
    }

    @PreUpdate
    protected void onUpdated(){
        this.updatedAt = LocalDateTime.now();
    }

}
