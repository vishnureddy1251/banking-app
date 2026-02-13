package com.banking.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "customers")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
}
