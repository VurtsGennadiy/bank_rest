package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Информация о балансе карты")
public class CardBalanceDto {
    @Schema(description = "номер карты", example = "**** **** **** 1234")
    String number;

    @Schema(description = "баланс карты", example = "1234.56")
    BigDecimal balance;
}
