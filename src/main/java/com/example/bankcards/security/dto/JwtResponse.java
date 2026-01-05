package com.example.bankcards.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ДТО класс для ответа на запрос авторизации. Содержит только токен jwt
 */
@Data
@AllArgsConstructor
public class JwtResponse {
    private String jwt;
}
