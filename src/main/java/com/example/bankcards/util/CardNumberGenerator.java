package com.example.bankcards.util;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class CardNumberGenerator {
    /**
     * Генерирует валидный 16-значный номер карты платёжной системы "Мир",
     * начинающийся с "2200" и проходящий проверку по алгоритму Луна.
     *
     * @return Валидный номер карты "Мир" как строка
     */
    public static String generateMirCardNumber() {
        Random random = new Random();
        StringBuilder number = new StringBuilder("2200"); // префикс Мир

        // Добавляем 11 случайных цифр (чтобы до контрольной цифры было 15)
        for (int i = 0; i < 11; i++) {
            number.append(random.nextInt(10));
        }

        // Вычисляем контрольную цифру по алгоритму Луна
        int checksum = 0;
        boolean doubleDigit = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));
            if (doubleDigit) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }
            checksum += digit;
            doubleDigit = !doubleDigit;
        }

        int checkDigit = (10 - (checksum % 10)) % 10;
        return number.append(checkDigit).toString();
    }
}
