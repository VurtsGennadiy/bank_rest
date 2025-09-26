package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "cards")
public class Card {
    @Id
    String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "created", nullable = false)
    final LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    CardStatus status = CardStatus.BLOCKED;

    @Column(name = "balance", precision = 19, scale = 2)
    BigDecimal balance = BigDecimal.ZERO;
}
