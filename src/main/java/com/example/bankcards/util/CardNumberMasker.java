package com.example.bankcards.util;

import lombok.experimental.UtilityClass;

/**
 * Утилитарный класс для маскирования номера карты
 * Маска карты: **** **** **** 1234
 */
@UtilityClass
public class CardNumberMasker {
    public static String mask(String number) {
        if (number == null || number.length() != 16) {
            throw new IllegalArgumentException("Card number must be 16 digits");
        }
        return number.replaceAll("(\\d{4})(\\d{4})(\\d{4})(\\d{4})", "**** **** **** $4");
    }
}
