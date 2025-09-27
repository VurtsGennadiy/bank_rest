package com.example.bankcards.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardNumberMaskerTest {
    @Test
    void mask() {
        String number = "1234567890123456";
        String masked = CardNumberMasker.mask(number);
        assertEquals("**** **** **** 3456", masked);
    }
}