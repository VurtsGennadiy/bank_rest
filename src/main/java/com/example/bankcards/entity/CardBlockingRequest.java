package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "card_blocking_requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CardBlockingRequest {
    @EqualsAndHashCode.Include
    @Id
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "card_number", nullable = false)
    String cardNumber;

    @Column(name = "created", nullable = false)
    private final LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

    @Column(name = "solved")
    boolean solved;
}
