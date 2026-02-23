package com.banking.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "account_events")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountEvent {
}
