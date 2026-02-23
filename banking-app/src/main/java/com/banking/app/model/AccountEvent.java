package com.banking.app.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_events")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "balance_after", nullable = false)
    private BigDecimal balanceAfter;

    @Column(name = "triggered_by")
    private String triggeredBy;

    @Column(name = "related_account_id")
    private Long relatedAccountId;

    @Column(name = "metadata", length = 500)
    private String metadata;

    @Column(name = "sequence_number", nullable = false)
    private Long sequenceNumber;

    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime eventTimestamp;

    @PrePersist
    protected void onCreate() {
        this.eventTimestamp = LocalDateTime.now();
    }
}
