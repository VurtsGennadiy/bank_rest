package com.example.bankcards.controller.adminAPI;

import com.example.bankcards.dto.CardBlockingRequestFullDto;
import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardFullDto;
import com.example.bankcards.dto.param.CardBlockingRequestSearchParam;
import com.example.bankcards.dto.param.CardSearchParam;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/card")
@RequiredArgsConstructor
@Validated
public class AdminCardController {
    private final CardService cardService;
    private final String datePattern = "yyyy-MM-dd";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardDto createNewCard(@RequestBody @Valid CardCreateRequest cardRequest) {
        return cardService.createNewCard(cardRequest);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@RequestBody String cardNumber) {
        cardService.deleteCard(cardNumber.trim());
    }

    @GetMapping
    public List<CardDto> getCards(@RequestParam(required = false) String number,
                                  @RequestParam(required = false) @Positive Long userId,
                                  @RequestParam(required = false) CardStatus status,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = datePattern) LocalDate created,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = datePattern) LocalDate expiration,
                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                  @RequestParam(defaultValue = "20") @Positive Integer size) {
        return cardService.getCards(
                CardSearchParam.builder()
                        .number(number)
                        .userId(userId)
                        .status(status)
                        .created(created)
                        .expiration(expiration)
                        .from(from)
                        .size(size)
                        .build()
        );
    }

    // Использую POST метод для получения данных по карте, для того чтобы не передавать чувствительную информацию в параметрах запроса
    @PostMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    public CardFullDto getCard(@RequestBody String cardNumber) {
        return cardService.getCard(cardNumber.trim());
    }

    @PutMapping("/activate")
    @ResponseStatus(HttpStatus.OK)
    public CardDto activateCard(@RequestBody String cardNumber) {
        return cardService.activateCard(cardNumber.trim());
    }

    @PutMapping("/block")
    @ResponseStatus(HttpStatus.OK)
    public CardDto blockCard(@RequestBody(required = false) String cardNumber,
                             @RequestParam(required = false) @Positive Long requestId) {
        if (requestId != null) {
            return cardService.blockCardByRequest(requestId);
        } else if (cardNumber != null) {
            return cardService.blockCardByNumber(cardNumber.trim());
        } else {
            throw new IllegalArgumentException("Необходимо указать id запроса на блокировку либо номер карты");
        }
    }

    @GetMapping("/blocking-request")
    public List<CardBlockingRequestFullDto> getBlockingRequests(@RequestParam(required = false) Boolean solved,
                                                                @RequestParam(required = false) @DateTimeFormat(pattern = datePattern) LocalDate createdFrom,
                                                                @RequestParam(required = false) @DateTimeFormat(pattern = datePattern) LocalDate createdTo,
                                                                @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                                @RequestParam(defaultValue = "20") @Positive Integer size) {
        return cardService.getCardBlockingRequests(
                CardBlockingRequestSearchParam.builder()
                        .solved(solved)
                        .createdFrom(createdFrom)
                        .createdTo(createdTo)
                        .from(from)
                        .size(size)
                        .build()
        );
    }
}
