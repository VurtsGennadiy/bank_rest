package com.example.bankcards.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/**
 * ДТО запроса на блокировку карты с информацией о владельце карты
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardBlockingRequestFullDto {
    Long id;

    UserDto user;

    String cardNumber;

    LocalDateTime created;

    boolean solved;
}
