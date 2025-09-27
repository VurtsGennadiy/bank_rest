package com.example.bankcards.dto.param;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * Фильтр для поиска запросов на блокировку карт
 */
@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardBlockingRequestSearchParam {
    LocalDate createdFrom;

    LocalDate createdTo;

    Boolean solved;

    Integer from;

    Integer size;
}
