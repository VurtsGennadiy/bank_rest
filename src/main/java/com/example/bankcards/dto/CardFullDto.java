package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Полная информация о карте, включающая описание владельца
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardFullDto {
    String number;

    UserDto owner;

    LocalDateTime created;

    LocalDate expiration;

    CardStatus status;

    BigDecimal balance;
}
