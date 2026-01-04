package com.example.bankcards.controller.adminAPI;

import com.example.bankcards.config.SecurityTestConfig;
import com.example.bankcards.dto.*;
import com.example.bankcards.dto.filters.CardBlockingRequestSearchParam;
import com.example.bankcards.dto.filters.CardSearchParam;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.UserRole;
import com.example.bankcards.exception.CardDeleteException;
import com.example.bankcards.exception.ErrorResponse;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.service.AdminCardService;
import com.example.bankcards.util.CardNumberMasker;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityTestConfig.class)
@AutoConfigureMockMvc
@WebMvcTest(AdminCardController.class)
class AdminCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminCardService cardService;


    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    void createNewCard_whenRequestIsValid_thenReturnCreatedCard() {
        CardCreateRequest cardRequest = new CardCreateRequest(
                2L, BigDecimal.valueOf(1000), LocalDate.now().plusYears(1L));

        CardDto cardDto = CardDto.builder()
                .number("**** **** **** 1234")
                .ownerId(cardRequest.getUserId())
                .balance(cardRequest.getBalance())
                .expiration(cardRequest.getExpiration())
                .status(CardStatus.BLOCKED)
                .build();

        when(cardService.createNewCard(cardRequest)).thenReturn(cardDto);

        String result = mockMvc.perform(post("/admin/cards")
                        .content(objectMapper.writeValueAsString(cardRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(cardDto), result);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    void createNewCard_whenUserIsNotExist_thenReturn404() {
        CardCreateRequest cardRequest = new CardCreateRequest(
                2L, BigDecimal.valueOf(1000), LocalDate.now().plusYears(1L));

        String errorMessage = String.format("User with id %s not found", cardRequest.getUserId());
        when(cardService.createNewCard(cardRequest)).thenThrow(new NotFoundException(errorMessage));

        String response = mockMvc.perform(post("/admin/cards")
                        .content(objectMapper.writeValueAsString(cardRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(new ErrorResponse(errorMessage)), response);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    void deleteCard_whenZeroBalance_thenReturn204() {
        String cardNumber = "1234567876543210";
        mockMvc.perform(delete("/admin/cards").content(cardNumber))
                .andExpectAll(status().isNoContent(), content().bytes(new byte[0]));

        verify(cardService, times(1)).deleteCard(cardNumber);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    void deleteCard_whenBalanceIsNotZero_thenReturn406() {
        String cardNumber = "1234567876543210";
        String errorMessage = String.format("Card with number %s has non-zero balance", CardNumberMasker.mask(cardNumber));
        doThrow(new CardDeleteException(errorMessage)).when(cardService).deleteCard(cardNumber);

        String response = mockMvc.perform(delete("/admin/cards").content(cardNumber))
                .andExpectAll(
                        status().isNotAcceptable(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(cardService, times(1)).deleteCard(cardNumber);
        assertEquals(objectMapper.writeValueAsString(new ErrorResponse(errorMessage)), response);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    void getCards() {
        mockMvc.perform(get("/admin/cards")
                        .param("userId", "2")
                        .param("status", "ACTIVE"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );

        verify(cardService, times(1))
                .getCards(CardSearchParam.builder()
                        .number(null)
                        .userId(2L)
                        .status(CardStatus.ACTIVE)
                        .created(null)
                        .expiration(null)
                        .from(0)
                        .size(20)
                        .build()
                );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    void getCard_ValidCardNumber_ReturnsCardFullDto() {
        String cardNumber = "1234567876543210";
        CardFullDto expectedCardFullDto = CardFullDto.builder()
                .number("**** **** **** 1234")
                .owner(UserDto.builder()
                        .id(2L)
                        .username("John Doe")
                        .email("john@example.com")
                        .role(UserRole.ROLE_USER)
                        .build())
                .created(LocalDateTime.now())
                .expiration(LocalDate.now().plusYears(1))
                .status(CardStatus.ACTIVE)
                .balance(new BigDecimal("1234.56"))
                .build();

        when(cardService.getCard(cardNumber)).thenReturn(expectedCardFullDto);

        String response = mockMvc.perform(post("/admin/cards/get")
                        .content(cardNumber))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(cardService, times(1)).getCard(cardNumber);
        assertEquals(objectMapper.writeValueAsString(expectedCardFullDto), response);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    void getCard_CardNotFound_ReturnsNotFound() {
        String cardNumber = "1234567876543210";
        String errorMessage = "Card with number **** **** **** 3210 not found";

        when(cardService.getCard(cardNumber)).thenThrow(new NotFoundException(errorMessage));

        mockMvc.perform(post("/admin/cards/get")
                        .content(cardNumber)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(errorMessage));

        verify(cardService, times(1)).getCard(cardNumber);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    void activateCard() {
        String cardNumber = "1234567876543210";
        CardDto cardDto = CardDto.builder()
                .number("**** **** **** 3210")
                .ownerId(2L)
                .created(LocalDateTime.now().minusHours(1))
                .expiration(LocalDate.now().plusYears(1))
                .status(CardStatus.ACTIVE)
                .balance(new BigDecimal("1234.56"))
                .build();

        when(cardService.activateCard(cardNumber)).thenReturn(cardDto);

        String response = mockMvc.perform(put("/admin/cards/activate").content(cardNumber))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(cardService, times(1)).activateCard(cardNumber);
        assertEquals(objectMapper.writeValueAsString(cardDto), response);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    void blockCard_ByRequestId_ReturnsCardDto() {
        Long requestId = 1L;
        CardDto expectedCardDto = CardDto.builder()
                .number("**** **** **** 1234")
                .ownerId(2L)
                .status(CardStatus.BLOCKED)
                .balance(new BigDecimal("1234.56"))
                .build();

        when(cardService.blockCardByRequest(requestId)).thenReturn(expectedCardDto);

        mockMvc.perform(put("/admin/cards/block")
                        .param("requestId", requestId.toString())
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("**** **** **** 1234"))
                .andExpect(jsonPath("$.status").value("BLOCKED"));

        verify(cardService, times(1)).blockCardByRequest(requestId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    void blockCard_ByCardNumber_ReturnsCardDto() {
        String cardNumber = "1234567876543210";
        CardDto expectedCardDto = CardDto.builder()
                .number("**** **** **** 3210")
                .ownerId(2L)
                .status(CardStatus.BLOCKED)
                .balance(new BigDecimal("1234.56"))
                .build();

        when(cardService.blockCardByNumber(cardNumber)).thenReturn(expectedCardDto);

        mockMvc.perform(put("/admin/cards/block")
                        .content(cardNumber)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("**** **** **** 3210"))
                .andExpect(jsonPath("$.status").value("BLOCKED"));

        verify(cardService, times(1)).blockCardByNumber(cardNumber);
        verify(cardService, times(0)).blockCardByRequest(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    void blockCard_WithoutParameters_ReturnsBadRequest() {
        mockMvc.perform(put("/admin/cards/block")
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Необходимо указать id запроса на блокировку либо номер карты"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    void getBlockingRequests_WithAllParameters_ReturnsListOfCardBlockingRequestFullDto() {
        Boolean solved = false;
        LocalDate createdFrom = LocalDate.of(2025, 9, 28);
        LocalDate createdTo = LocalDate.of(2025, 9, 29);
        Integer from = 0;
        Integer size = 20;

        List<CardBlockingRequestFullDto> expectedResult = List.of(
                CardBlockingRequestFullDto.builder()
                        .id(1L)
                        .cardNumber("**** **** **** 1234")
                        .created(LocalDateTime.of(2025, 9, 28, 12, 0))
                        .solved(false)
                        .build()
        );

        when(cardService.getCardBlockingRequests(any(CardBlockingRequestSearchParam.class))).thenReturn(expectedResult);

        mockMvc.perform(get("/admin/cards/blocking-request")
                        .param("solved", solved.toString())
                        .param("createdFrom", createdFrom.toString())
                        .param("createdTo", createdTo.toString())
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].cardNumber").value("**** **** **** 1234"));

        verify(cardService, times(1)).getCardBlockingRequests(
                argThat(param -> param.getSolved().equals(solved) &&
                        param.getCreatedFrom().equals(createdFrom) &&
                        param.getCreatedTo().equals(createdTo) &&
                        param.getFrom().equals(from) &&
                        param.getSize().equals(size))
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    @SneakyThrows
    void blockCard_whenUnauthorized_thenReturnStatus403() {
        mockMvc.perform(put("/admin/cards/block"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @SneakyThrows
    void activateCard_whenUnauthorized_thenReturnStatus403() {
        mockMvc.perform(put("/admin/cards/activate"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @SneakyThrows
    void getCards_whenUnauthorized_thenReturnStatus403() {
        mockMvc.perform(get("/admin/cards"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @SneakyThrows
    void getCard_whenUnauthorized_thenReturnStatus403() {
        mockMvc.perform(post("/admin/cards/get"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @SneakyThrows
    void createNewCard_whenUnauthorized_thenReturnStatus403() {
        mockMvc.perform(post("/admin/cards"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @SneakyThrows
    void deleteCard_whenUnauthorized_thenReturnStatus403() {
        mockMvc.perform(delete("/admin/cards"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @SneakyThrows
    void getBlockingRequests_whenUnauthorized_thenReturnStatus403() {
        mockMvc.perform(delete("/admin/cards/blocking-request"))
                .andExpect(status().isForbidden());
    }
}