package com.example.bankcards.service;

import com.example.bankcards.dto.CardBalanceDto;
import com.example.bankcards.dto.CardBlockingRequestDto;
import com.example.bankcards.dto.MoneyTransferRequest;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.MoneyTransferException;
import com.example.bankcards.repository.CardBlockingRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.MoneyTransferRepository;
import com.example.bankcards.util.mapper.CardBlockingRequestMapper;
import com.example.bankcards.util.mapper.CardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCardServiceImplTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardBlockingRequestRepository cardBlockingRequestRepository;
    @Mock
    private MoneyTransferRepository moneyTransferRepository;
    @Mock
    private CardMapper cardMapper;
    @Mock
    private CardBlockingRequestMapper cardBlockingRequestMapper;
    @InjectMocks
    private UserCardServiceImpl userCardService;

    private Card card;
    private Card toCard;
    private User user;
    private Long userId = 1L;
    String cardNumber = "1234567890123456";
    String cardMaskedNumber = "**** **** **** 3456";
    String cardPartNumber = "3456";

    String toCardNumber = "1234123412341234";
    String toCardMaskedNumber = "**** **** **** 1234";
    String toCardPartNumber = "1234";

    @BeforeEach()
    void setTestData() {
        user = new User();
        user.setUsername("username");
        user.setEmail("username@mail.ru");
        user.setRole(UserRole.ROLE_USER);
        user.setId(userId);
        user.setPassword("password123");

        card = new Card();
        card.setNumber(cardNumber);
        card.setOwner(user);
        card.setStatus(CardStatus.ACTIVE);
        card.setExpiration(card.getCreated().plusYears(1L).toLocalDate());
        card.setBalance(BigDecimal.valueOf(1000));

        toCard = new Card();
        toCard.setNumber(toCardNumber);
        toCard.setOwner(user);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setExpiration(card.getCreated().plusYears(1L).toLocalDate());
        toCard.setBalance(BigDecimal.valueOf(1000));
    }

    @Test
    void getBalance_whenCardExist_thenReturnCardBalanceDto() {
        when(cardRepository.findCardByOwner_IdAndNumberContaining(userId, cardPartNumber)).thenReturn(Optional.of(card));

        CardBalanceDto expected = new CardBalanceDto(cardMaskedNumber, card.getBalance());
        CardBalanceDto actual = userCardService.getBalance(userId, cardPartNumber);
        assertEquals(expected, actual);
    }

    @Test
    void createCardBlockingRequest_whenCardExist_thenCardBlockingRequestSaved() {
        CardBlockingRequest request = new CardBlockingRequest();
        request.setId(1L);
        request.setUser(user);
        request.setCardNumber(cardNumber);

        CardBlockingRequestDto expected = new CardBlockingRequestDto(cardMaskedNumber, request.getCreated(), request.isSolved());
        when(cardRepository.findCardByOwner_IdAndNumberContaining(userId, cardPartNumber)).thenReturn(Optional.of(card));
        when(cardBlockingRequestMapper.toDto(any(CardBlockingRequest.class))).thenReturn(expected);

        CardBlockingRequestDto actual = userCardService.createCardBlockingRequest(userId, cardPartNumber);
        assertEquals(expected, actual);
    }

    @Test
    void cardToCardTransfer_whenCardsBalanceAndStatusOk_thenTransferMoney() {
        BigDecimal cardStartBalance = card.getBalance();
        BigDecimal toCardStartBalance = toCard.getBalance();

        MoneyTransferRequest request = new MoneyTransferRequest();
        request.setUserId(userId);
        request.setFromCardNumber(cardPartNumber);
        request.setToCardNumber(toCardPartNumber);
        request.setAmount(BigDecimal.valueOf(100));

        when(cardRepository.findCardByOwner_IdAndNumberContaining(userId, cardPartNumber)).thenReturn(Optional.of(card));
        when(cardRepository.findCardByOwner_IdAndNumberContaining(userId, toCardPartNumber)).thenReturn(Optional.of(toCard));

        CardBalanceDto expectedDto = new CardBalanceDto(cardMaskedNumber, cardStartBalance.subtract(request.getAmount()));
        CardBalanceDto actualDto = userCardService.cardToCardTransfer(request);

        assertEquals(cardStartBalance.subtract(request.getAmount()), card.getBalance());
        assertEquals(toCardStartBalance.add(request.getAmount()), toCard.getBalance());
        assertEquals(expectedDto, actualDto);
        verify(moneyTransferRepository).save(any());
    }

    @Test
    void cardToCardTransfer_whenInsufficientBalanceCard_thenThrowException() {
        BigDecimal cardStartBalance = card.getBalance();
        BigDecimal toCardStartBalance = toCard.getBalance();

        MoneyTransferRequest request = new MoneyTransferRequest();
        request.setUserId(userId);
        request.setFromCardNumber(cardPartNumber);
        request.setToCardNumber(toCardPartNumber);
        request.setAmount(BigDecimal.valueOf(10000));

        when(cardRepository.findCardByOwner_IdAndNumberContaining(userId, cardPartNumber)).thenReturn(Optional.of(card));
        when(cardRepository.findCardByOwner_IdAndNumberContaining(userId, toCardPartNumber)).thenReturn(Optional.of(toCard));

        MoneyTransferException exception = assertThrows(MoneyTransferException.class,
                () -> userCardService.cardToCardTransfer(request));

        assertTrue(exception.getMessage().contains("Insufficient funds"));
        assertEquals(cardStartBalance, card.getBalance());
        assertEquals(toCardStartBalance, toCard.getBalance());
        verify(moneyTransferRepository, never()).save(any());
    }

    @Test
    void cardToCardTransfer_whenToCardStatusNotActive_thenThrowException() {
        toCard.setStatus(CardStatus.BLOCKED);
        BigDecimal cardStartBalance = card.getBalance();
        BigDecimal toCardStartBalance = toCard.getBalance();

        MoneyTransferRequest request = new MoneyTransferRequest();
        request.setUserId(userId);
        request.setFromCardNumber(cardPartNumber);
        request.setToCardNumber(toCardPartNumber);
        request.setAmount(BigDecimal.valueOf(100));

        when(cardRepository.findCardByOwner_IdAndNumberContaining(userId, cardPartNumber)).thenReturn(Optional.of(card));
        when(cardRepository.findCardByOwner_IdAndNumberContaining(userId, toCardPartNumber)).thenReturn(Optional.of(toCard));

        MoneyTransferException exception = assertThrows(MoneyTransferException.class,
                () -> userCardService.cardToCardTransfer(request));

        assertTrue(exception.getMessage().contains(toCardMaskedNumber + " status is not ACTIVE"));
        assertEquals(cardStartBalance, card.getBalance());
        assertEquals(toCardStartBalance, toCard.getBalance());
        verify(moneyTransferRepository, never()).save(any());
    }

    @Test
    void cardToCardTransfer_whenCardStatusNotActive_thenThrowException() {
        card.setStatus(CardStatus.BLOCKED);
        BigDecimal cardStartBalance = card.getBalance();
        BigDecimal toCardStartBalance = toCard.getBalance();

        MoneyTransferRequest request = new MoneyTransferRequest();
        request.setUserId(userId);
        request.setFromCardNumber(cardPartNumber);
        request.setToCardNumber(toCardPartNumber);
        request.setAmount(BigDecimal.valueOf(100));

        when(cardRepository.findCardByOwner_IdAndNumberContaining(userId, cardPartNumber)).thenReturn(Optional.of(card));
        when(cardRepository.findCardByOwner_IdAndNumberContaining(userId, toCardPartNumber)).thenReturn(Optional.of(toCard));

        MoneyTransferException exception = assertThrows(MoneyTransferException.class,
                () -> userCardService.cardToCardTransfer(request));

        assertTrue(exception.getMessage().contains(cardMaskedNumber + " status is not ACTIVE"));
        assertEquals(cardStartBalance, card.getBalance());
        assertEquals(toCardStartBalance, toCard.getBalance());
        verify(moneyTransferRepository, never()).save(any());
    }
}