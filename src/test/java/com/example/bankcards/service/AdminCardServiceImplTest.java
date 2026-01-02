package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardFullDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.CardDeleteException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardBlockingRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.mapper.CardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCardServiceImplTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CardBlockingRequestRepository cardBlockingRequestRepository;
    @Mock
    private CardMapper cardMapper;
    @InjectMocks
    private AdminCardServiceImpl adminCardService;

    String cardNumber = "1234567890123456";
    String maskedNumber = "**** **** **** 3456";
    private User user;
    private Card card;
    private CardFullDto cardFullDto;
    private CardDto cardDto;

    @BeforeEach
    void setTestData() {
        user = new User();
        user.setUsername("username");
        user.setEmail("username@mail.ru");
        user.setRole(UserRole.USER);
        user.setId(1L);
        user.setPassword("password123");

        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole());

        card = new Card();
        card.setNumber(cardNumber);
        card.setOwner(user);
        card.setStatus(CardStatus.BLOCKED);
        card.setExpiration(card.getCreated().plusYears(1L).toLocalDate());
        card.setBalance(BigDecimal.valueOf(1000));

        cardFullDto = CardFullDto.builder()
                .number(maskedNumber)
                .owner(userDto)
                .created(card.getCreated())
                .expiration(card.getExpiration())
                .status(card.getStatus())
                .balance(card.getBalance())
                .build();

        cardDto = CardDto.builder()
                .number(maskedNumber)
                .ownerId(user.getId())
                .created(card.getCreated())
                .status(card.getStatus())
                .expiration(card.getExpiration())
                .balance(card.getBalance())
                .build();
    }


    @Test
    void getCard_whenCardExists_thenReturnCardFullDto() {
        when(cardRepository.findByNumberJoinOwner(cardNumber)).thenReturn(Optional.of(card));
        when(cardMapper.toCardFullDto(card, maskedNumber)).thenReturn(cardFullDto);

        CardFullDto actual = adminCardService.getCard(cardNumber);

        assertNotNull(actual);
        assertEquals(cardFullDto, actual);
        verify(cardRepository).findByNumberJoinOwner(cardNumber);
        verify(cardMapper).toCardFullDto(card, maskedNumber);
    }

    @Test
    void getCard_whenCardNotExists_thenThrowNotFoundException() {
        when(cardRepository.findByNumberJoinOwner(cardNumber)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            adminCardService.getCard(cardNumber);
        });

        assertTrue(exception.getMessage().contains(maskedNumber));
        assertFalse(exception.getMessage().contains(cardNumber));
        verify(cardRepository).findByNumberJoinOwner(cardNumber);
    }

    @Test
    void createNewCard_whenValidRequest_thenCardSaved() {
        CardCreateRequest cardRequest = new CardCreateRequest(
                1L, BigDecimal.valueOf(1000), LocalDate.now().plusYears(1));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(cardRepository.existsById(anyString())).thenReturn(false);
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toCardDto(card, maskedNumber)).thenReturn(cardDto);

        CardDto actual = adminCardService.createNewCard(cardRequest);

        assertNotNull(actual);
        assertEquals(cardDto, actual);
        verify(cardRepository).existsById(anyString());
        verify(cardRepository).save(card);
    }

    @Test
    void activateCard_whenCardExists_thenCardActivated() {
        card.setStatus(CardStatus.BLOCKED);

        CardDto expected = cardDto;
        cardDto.setStatus(CardStatus.ACTIVE);
        when(cardRepository.findById(cardNumber)).thenReturn(Optional.of(card));
        when(cardMapper.toCardDto(card, maskedNumber)).thenReturn(expected);

        CardDto actual = adminCardService.activateCard(cardNumber);

        assertEquals(expected, actual);
    }

    @Test
    void blockCardByNumber_whenCardExists_thenCardBlocked() {
        card.setStatus(CardStatus.ACTIVE);

        CardDto expected = cardDto;
        cardDto.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findById(cardNumber)).thenReturn(Optional.of(card));
        when(cardMapper.toCardDto(card, maskedNumber)).thenReturn(expected);

        CardDto actual = adminCardService.blockCardByNumber(cardNumber);

        assertEquals(CardStatus.BLOCKED, card.getStatus());
        assertEquals(expected, actual);
    }

    @Test
    void blockCardByRequest_whenRequestExists_thenCardBlocked() {
        card.setStatus(CardStatus.ACTIVE);

        Long requestId = 1L;
        CardBlockingRequest request = new CardBlockingRequest();
        request.setId(requestId);
        request.setUser(user);
        request.setCardNumber(cardNumber);
        request.setSolved(false);

        CardDto expected = cardDto;
        cardDto.setStatus(CardStatus.BLOCKED);

        when(cardBlockingRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(cardRepository.findById(cardNumber)).thenReturn(Optional.of(card));
        when(cardMapper.toCardDto(card, maskedNumber)).thenReturn(expected);
        CardDto actual = adminCardService.blockCardByRequest(requestId);

        assertTrue(request.isSolved());
        assertEquals(CardStatus.BLOCKED, card.getStatus());
        assertEquals(expected, actual);
    }

    @Test
    void deleteCard_whenZeroBalance_thenCardDeleted() {
        card.setBalance(BigDecimal.ZERO);
        when(cardRepository.findById(cardNumber)).thenReturn(Optional.of(card));

        adminCardService.deleteCard(cardNumber);

        verify(cardRepository).delete(card);
    }

    @Test
    void deleteCard_whenNonZeroBalance_thenThrowCardDeleteException() {
        card.setBalance(BigDecimal.valueOf(1000));
        when(cardRepository.findById(cardNumber)).thenReturn(Optional.of(card));

        CardDeleteException exception = assertThrows(CardDeleteException.class, () -> {
            adminCardService.deleteCard(cardNumber);
        });

        assertFalse(exception.getMessage().contains(cardNumber));
        verify(cardRepository, never()).delete(card);
    }

}