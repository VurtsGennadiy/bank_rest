package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Параметры перевода средств между счетами")
public class MoneyTransferRequest {
    @Schema(description = "id пользователя", example = "1")
    Long userId;

    @Schema(description = "часть номера карты, с которой осуществляется перевод (достаточно указать последние 4 цифры)",
            example = "1234")
    String fromCardNumber;

    @Schema(description = "часть номера карты, на которую осуществляется перевод (достаточно указать последние 4 цифры)",
            example = "5678")
    String toCardNumber;

    @Positive
    @Schema(description = "сумма перевода", example = "100")
    BigDecimal amount;
}
