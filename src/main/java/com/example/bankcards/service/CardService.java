package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardFullDto;
import com.example.bankcards.dto.param.CardSearchParam;

import java.util.List;

public interface CardService {
    CardDto createNewCard(CardCreateRequest cardRequest);

    CardDto activateCard(String cardNumber);

    CardDto blockCard(String cardNumber);

    void deleteCard(String cardNumber);

    List<CardDto> getCards(CardSearchParam params);

    CardFullDto getCard(String cardNumber);
}
