package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardShortDto {
    String number;

    LocalDateTime created;

    CardStatus status;

    BigDecimal balance;
}
