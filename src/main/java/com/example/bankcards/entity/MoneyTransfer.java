package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "money_transfer")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MoneyTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id")
    Long userId;

    @Column(name = "from_card_number")
    String fromCardNumber;

    @Column(name = "to_card_number")
    String toCardNumber;

    @Column(name = "amount")
    BigDecimal amount;

    @Column(name = "created")
    final LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
}
