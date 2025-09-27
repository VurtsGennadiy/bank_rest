package com.example.bankcards.dto;

import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * ДТО содержащее параметры перевода между счетами
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class MoneyTransferRequest {
    Long userId;

    String fromCardNumber;

    String toCardNumber;

    @Positive
    BigDecimal amount;
}
