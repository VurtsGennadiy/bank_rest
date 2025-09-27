package com.example.bankcards.controller.userAPI;

import com.example.bankcards.dto.CardBlockingRequestDto;
import com.example.bankcards.dto.CardShortDto;
import com.example.bankcards.dto.param.CardSearchParam;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.UserCardService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/user/card")
@RequiredArgsConstructor
@Validated
public class UserCardController {
    private final UserCardService userCardService;
    private final String datePattern = "yyyy-MM-dd";
    private static final String USER_ID_HEADER = "X-User-Id";

    @GetMapping
    public List<CardShortDto> getCards(@RequestParam(required = false) String number,
                                       @RequestParam(required = false) CardStatus status,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = datePattern) LocalDate created,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = datePattern) LocalDate expiration,
                                       @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                       @RequestParam(defaultValue = "20") @Positive Integer size,
                                       @RequestHeader(USER_ID_HEADER) @Positive Long userId) {
        return userCardService.getCards(
                CardSearchParam.builder()
                        .userId(userId)
                        .number(number)
                        .status(status)
                        .created(created)
                        .expiration(expiration)
                        .from(from)
                        .size(size)
                        .build()
        );
    }

    @PostMapping("/block")
    public CardBlockingRequestDto createBlockingCardRequest(@RequestBody String partCardNumber,
                                                            @RequestHeader(USER_ID_HEADER) @Positive Long userId) {
        return userCardService.createCardBlockingRequest(userId, partCardNumber.trim());
    }
}
