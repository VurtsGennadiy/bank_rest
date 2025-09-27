package com.example.bankcards.exception;

/**
 * Исключение, возникающее при попытке удалить карту с ненулевым балансом
 */
public class CardDeleteException extends RuntimeException {
    public CardDeleteException(String message) {
        super(message);
    }
}
