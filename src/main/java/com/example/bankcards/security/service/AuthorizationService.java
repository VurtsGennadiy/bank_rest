package com.example.bankcards.security.service;

import com.example.bankcards.security.dto.JwtRequest;
import com.example.bankcards.security.dto.JwtResponse;

/**
 * Сервис аутентификации
 */
public interface AuthorizationService {
    /**
     * Аутентификация пользователя по логину и паролю.
     * В случае успешной аутентификации генерирует и возвращает jwt - токен.
     */
    JwtResponse authorize(JwtRequest authRequest);
}
