package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardDto {
    String number;

    Long ownerId;

    LocalDateTime created;

    LocalDate expiration;

    CardStatus status;

    BigDecimal balance;
}
