package com.banking.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "loan_type", nullable = false)
    @NotBlank(message = "Loan type is required (PERSONAL, HOME, AUTO, EDUCATION)")
    private String loanType;

    @Column(name = "loan_amount", nullable = false)
    @DecimalMin(value = "100.0", message = "Minimum loan amount is $100")
    private BigDecimal loanAmount;

    @Column(name = "amount_paid", nullable = false)
    private BigDecimal amountPaid;

    @Column(name = "remaining_balance")
    private BigDecimal remainingBalance;

    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    @Column(name = "tenure_months", nullable = false)
    private Integer tenureMonths;

    @Column(name = "status", nullable = false)
    private String status = "PENDING";

    @Column(name = "applied_date")
    private LocalDateTime appliedDate;

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    @Column(name = "updated_at")
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
