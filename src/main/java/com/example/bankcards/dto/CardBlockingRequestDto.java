package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/**
 * ДТО запроса на блокировку карты, не содержит данные владельца
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Объект запроса пользователя на блокировку карты")
public class CardBlockingRequestDto {
    @Schema(description = "номер карты", example = "**** **** **** 1234")
    String cardNumber;

    @Schema(description = "дата и время создания запроса", example = "2025-09-29T12:00")
    LocalDateTime created;

    @Schema(description = "статус решения запроса", example = "false")
    boolean solved;
}
