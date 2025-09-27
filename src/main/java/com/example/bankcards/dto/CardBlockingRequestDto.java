package com.example.bankcards.dto;

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
public class CardBlockingRequestDto {
    String cardNumber;

    LocalDateTime created;

    boolean solved;
}
