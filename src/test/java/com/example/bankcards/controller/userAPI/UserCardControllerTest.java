package com.example.bankcards.controller.userAPI;

import com.example.bankcards.dto.CardBalanceDto;
import com.example.bankcards.dto.CardBlockingRequestDto;
import com.example.bankcards.dto.CardShortDto;
import com.example.bankcards.dto.MoneyTransferRequest;
import com.example.bankcards.dto.filters.CardSearchParam;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.ErrorResponse;
import com.example.bankcards.exception.MoneyTransferException;
import com.example.bankcards.service.UserCardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserCardController.class)
class UserCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserCardService userCardService;

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final Long TEST_USER_ID = 1L;

    @Test
    @SneakyThrows
    void getCards_ValidRequest_ReturnsCardShortDtoList() {
        List<CardShortDto> expectedCards = List.of(
                CardShortDto.builder()
                        .number("**** **** **** 1234")
                        .created(LocalDateTime.now())
                        .expiration(LocalDate.now().plusYears(1))
                        .status(CardStatus.ACTIVE)
                        .build());

        when(userCardService.getCards(any(CardSearchParam.class))).thenReturn(expectedCards);

        String response = mockMvc.perform(get("/user/cards")
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .param("number", "1234")
                        .param("status", "ACTIVE")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userCardService, times(1)).getCards(any(CardSearchParam.class));
        assertEquals(objectMapper.writeValueAsString(expectedCards), response);
    }

    @Test
    @SneakyThrows
    void createBlockingCardRequest_ValidRequest_ReturnsCardBlockingRequestDto() {
        String partCardNumber = "1234";

        CardBlockingRequestDto expectedDto = CardBlockingRequestDto.builder()
                .cardNumber("**** **** **** 1234")
                .created(LocalDateTime.now())
                .solved(false)
                .build();

        when(userCardService.createCardBlockingRequest(TEST_USER_ID, partCardNumber)).thenReturn(expectedDto);

        String response = mockMvc.perform(post("/user/cards/block")
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .content(partCardNumber)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userCardService, times(1)).createCardBlockingRequest(TEST_USER_ID, partCardNumber);
        assertEquals(objectMapper.writeValueAsString(expectedDto), response);
    }

    @Test
    @SneakyThrows
    void getCardBalance_ValidRequest_ReturnsCardBalanceDto() {
        String partCardNumber = "1234";
        CardBalanceDto expectedDto = CardBalanceDto.builder()
                .balance(BigDecimal.valueOf(1234.56))
                .number("**** **** **** 1234")
                .build();

        when(userCardService.getBalance(TEST_USER_ID, partCardNumber))
                .thenReturn(expectedDto);

        String response = mockMvc.perform(post("/user/cards/balance")
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .content(partCardNumber)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userCardService, times(1)).getBalance(TEST_USER_ID, partCardNumber);
        assertEquals(objectMapper.writeValueAsString(expectedDto), response);
    }

    @Test
    @SneakyThrows
    void transferMoney_ValidRequest_ReturnsCardBalanceDto() {
        MoneyTransferRequest transferRequest = MoneyTransferRequest.builder()
                .fromCardNumber("1234")
                .toCardNumber("5678")
                .amount(new BigDecimal("100.00"))
                .build();

        CardBalanceDto expectedDto = CardBalanceDto.builder()
                .number("**** **** **** 1234")
                .balance(new BigDecimal("1234.56"))
                .build();

        when(userCardService.cardToCardTransfer(any())).thenReturn(expectedDto);

        String response = mockMvc.perform(post("/user/cards/transfer")
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .content(objectMapper.writeValueAsString(transferRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userCardService, times(1)).cardToCardTransfer(any());
        assertEquals(objectMapper.writeValueAsString(expectedDto), response);
    }

    @Test
    @SneakyThrows
    void transferMoney_whenInsufficientFunds_thenReturn406() {
        MoneyTransferRequest transferRequest = MoneyTransferRequest.builder()
                .fromCardNumber("1234")
                .toCardNumber("5678")
                .amount(new BigDecimal("100.00"))
                .build();

        String errorMessage = "Insufficient funds on the card to perform the operation";

        when(userCardService.cardToCardTransfer(any())).thenThrow(new MoneyTransferException(errorMessage));

        String response = mockMvc.perform(post("/user/cards/transfer")
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .content(objectMapper.writeValueAsString(transferRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userCardService, times(1)).cardToCardTransfer(any());
        assertEquals(objectMapper.writeValueAsString(new ErrorResponse(errorMessage)), response);
    }
}
