package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Данные для создания новой карты")
public class CardCreateRequest {
    @Positive
    @Schema(description = "id пользователя", example = "1")
    Long userId;

    @Schema(description = "начальный баланс карты", example = "1000.00")
    BigDecimal balance;

    @Future
    @Schema(description = "дата окончания срока действия карты", example = "2026-01-01")
    LocalDate expiration;
}
