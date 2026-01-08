package com.example.bankcards.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ДТО класс для ответа на запрос авторизации. Содержит только токен jwt
 */
@Data
@AllArgsConstructor
@Schema(description = "Ответ на успешную аутентификацию")
public class JwtResponse {

    @Schema(description = "Токен JWT")
    private String jwt;
}
