package com.example.bankcards.dto.filters;

import com.example.bankcards.entity.CardStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * Фильтр для поиска карт
 */
@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardSearchParam {
    String number;

    Long userId;

    CardStatus status;

    LocalDate created;

    LocalDate expiration;

    Integer from;

    Integer size;
}
