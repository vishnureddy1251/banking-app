package com.banking.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bill_payment")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "bill_type", nullable = false)
    @NotBlank(message = "Bill type is required")
    private String billType;

    @Column(name = "provider_name", nullable = false)
    @NotBlank(message = "Provider name is required")
    private String providerName;

    @Column(name = "consumer_number", nullable = false)
    @NotBlank(message = "Consumer number is required")
    private String consumerNumber;

    @Column(name = "amount", nullable = false)
    @DecimalMin(value = "1.0", message = "Amount must be at least $1")
    private BigDecimal amount;

    @Column(name = "status")
    private String status = "PENDING";

    @Column(name = "reference_number", unique = true)
    private String referenceNumber;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @PrePersist
    protected void onCreate() {
        this.paymentDate = LocalDateTime.now();
    }
}
