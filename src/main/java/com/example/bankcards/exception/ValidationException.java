package com.example.bankcards.exception;

/**
 * Кастомное исключение для ошибок валидации
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
