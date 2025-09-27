package com.example.bankcards.service;

import com.example.bankcards.dto.CardBlockingRequestFullDto;
import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardFullDto;
import com.example.bankcards.dto.filters.CardBlockingRequestSearchParam;
import com.example.bankcards.dto.filters.CardSearchParam;

import java.util.List;

public interface CardService {
    CardDto createNewCard(CardCreateRequest cardRequest);

    CardDto activateCard(String cardNumber);

    CardDto blockCardByNumber(String cardNumber);

    CardDto blockCardByRequest(Long cardBlockingRequestId);

    void deleteCard(String cardNumber);

    List<CardDto> getCards(CardSearchParam params);

    CardFullDto getCard(String cardNumber);

    List<CardBlockingRequestFullDto> getCardBlockingRequests(CardBlockingRequestSearchParam params);
}
