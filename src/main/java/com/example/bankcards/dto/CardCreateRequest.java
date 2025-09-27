package com.example.bankcards.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ДТО с необходимыми параметрами для создания новой карты
 */
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardCreateRequest {
    @Positive
    Long userId;

    BigDecimal balance;

    @Future
    LocalDate expiration;
}
