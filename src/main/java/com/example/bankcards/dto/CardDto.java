package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Информация о банковской карте")
public class CardDto {
    @Schema(description = "номер карты", example = "**** **** **** 1234")
    String number;

    @Schema(description = "id владельца карты", example = "1")
    Long ownerId;

    @Schema(description = "дата создания карты", example = "2025-09-28T12:00")
    LocalDateTime created;

    @Schema(description = "дата окончания срока действия карты", example = "2026-09-28")
    LocalDate expiration;

    @Schema(description = "статус карты", example = "ACTIVE")
    CardStatus status;

    @Schema(description = "баланс карты", example = "1234.56")
    BigDecimal balance;
}
