package com.example.bankcards.service;

import com.example.bankcards.dto.CardBalanceDto;
import com.example.bankcards.dto.CardBlockingRequestDto;
import com.example.bankcards.dto.CardShortDto;
import com.example.bankcards.dto.MoneyTransferRequest;
import com.example.bankcards.dto.filters.CardSearchParam;

import java.util.List;

/**
 * Сервис пользователя для операций с картами
 */
public interface UserCardService {
    List<CardShortDto> getCards(CardSearchParam params);

    CardBlockingRequestDto createCardBlockingRequest(Long userId, String partCardNumber);

    CardBalanceDto getBalance(Long userId, String partCardNumber);

    CardBalanceDto cardToCardTransfer(MoneyTransferRequest request);
}
