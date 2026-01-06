package com.example.bankcards.security.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * DTO класс для запроса на аутентификацию
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class JwtRequest {
    String username;
    String password;
}
