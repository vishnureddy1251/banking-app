package com.banking.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_holder_name", nullable = false)
    @NotBlank(message = "Account holder name is required")
    private String accountName;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "account_type", nullable = false)
    @NotBlank(message = "Account type is required (Savings or Checking)")
    private String accountType;

    @Column(name = "balance", nullable = false)
    @DecimalMin(value = "0.0", message = "Balance cannot be negative")
    private BigDecimal balance;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
