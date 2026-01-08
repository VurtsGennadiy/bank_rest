package com.example.bankcards.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Запрос аутентификации")
public class JwtRequest {
    @Schema(description = "имя пользователя", example = "username")
    String username;

    @Schema(description = "пароль пользователя", example = "very_secret_password")
    String password;
}
