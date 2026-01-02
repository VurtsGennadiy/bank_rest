package com.example.bankcards.dto.filters;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Фильтр для поиска пользователей
 */
@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSearchParam {
    String username;

    String email;

    Integer from;

    Integer size;
}
