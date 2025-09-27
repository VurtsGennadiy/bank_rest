package com.example.bankcards.exception;

/**
 * Исключение, возникающее при ошибке перевода денежных средств
 */
public class MoneyTransferException extends RuntimeException {
    public MoneyTransferException(String message) {
        super(message);
    }
}
