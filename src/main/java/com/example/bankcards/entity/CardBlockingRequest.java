package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "card_blocking_requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class CardBlockingRequest {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "card_number", nullable = false)
    String cardNumber;

    @Column(name = "created", nullable = false)
    private final LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

    @Column(name = "solved")
    boolean solved = false;
}
