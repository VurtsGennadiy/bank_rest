package com.example.bankcards.controller.userAPI;

import com.example.bankcards.dto.CardBalanceDto;
import com.example.bankcards.dto.CardBlockingRequestDto;
import com.example.bankcards.dto.CardShortDto;
import com.example.bankcards.dto.MoneyTransferRequest;
import com.example.bankcards.dto.filters.CardSearchParam;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.ErrorResponse;
import com.example.bankcards.service.UserCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(
        name = "User: карты",
        description = "Пользовательский API для действий с картами"
)
public class UserCardController {
    private final UserCardService userCardService;
    private final String datePattern = "yyyy-MM-dd";
    private static final String USER_ID_HEADER = "X-User-Id";

    @Operation(summary = "Поиск карт пользователя", description = "Поддерживается фильтрация и пагинация",
            parameters = {
                    @Parameter(name = "number", description = "Номер карты, поддерживается поиск по части номера карты", example = "1234"),
                    @Parameter(name = "status", description = "Статус карты", example = "ACTIVE"),
                    @Parameter(name = "created", description = "Дата создания карты", example = "2025-09-28"),
                    @Parameter(name = "expiration", description = "Дата окончания срока действия карты", example = "2026-09-28"),
                    @Parameter(name = "from", description = "Индекс первого элемента, с которого нужно вернуть"),
                    @Parameter(name = "size", description = "Количество возвращаемых элементов"),
                    @Parameter(name = "X-User-Id", description = "Id пользователя", example = "1", in = ParameterIn.HEADER)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список найденных карт"),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class,
                                    example = """
                                            {"error": "from должно быть не меньше 0."}""")))
            })
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

    @Operation(summary = "Создать запрос на блокировку карты",
            parameters = {
                    @Parameter(name = "X-User-Id", description = "Id пользователя", example = "1", in = ParameterIn.HEADER)},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Частичный номер карты (например последние 4 цифры)",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class, example = "1234"))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос на блокировку карты создан"),
                    @ApiResponse(responseCode = "404", description = "Карта не найдена",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class,
                                    example = """
                                            {"error": "Card with number 6016 not found"}"""))),
                    @ApiResponse(responseCode = "409", description = "Ошибка уникальности или целостности данных",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class,
                                    example = """
                                            {"error": "Запрос на блокировку для этой карты уже существует"}""")))
            })
    @PostMapping("/block")
    public CardBlockingRequestDto createBlockingCardRequest(@RequestBody String partCardNumber,
                                                            @RequestHeader(USER_ID_HEADER) @Positive Long userId) {
        return userCardService.createCardBlockingRequest(userId, partCardNumber.trim());
    }

    @Operation(summary = "Получить баланс карты",
            description = "Использую POST метод для получения данных по карте, для того чтобы не передавать чувствительную " +
                    "информацию в параметрах запроса",
            parameters = {
                    @Parameter(name = "X-User-Id", description = "Id пользователя", example = "1", in = ParameterIn.HEADER)},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Частичный номер карты (например последние 4 цифры)",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class, example = "1234")))
    )
    @PostMapping("/balance")
    public CardBalanceDto getCardBalance(@RequestBody String partCardNumber,
                                         @RequestHeader(USER_ID_HEADER) @Positive Long userId) {
        return userCardService.getBalance(userId, partCardNumber.trim());
    }

    @Operation(summary = "Перевести деньги между картами",
            parameters = {
                    @Parameter(name = "X-User-Id", description = "Id пользователя", example = "1", in = ParameterIn.HEADER)},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Параметры перевода. Используются частичные номера карт. " +
                            "Обе карты должны иметь статус ACTIVE. Сумма перевода должна быть положительная",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MoneyTransferRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Перевод выполнен"),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class,
                                    example = """
                                            {"error": "Поле amount должно быть больше 0."}"""))),
                    @ApiResponse(responseCode = "404", description = "Карта не найдена",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class,
                                    example = """
                                            {"error": "Card with number 6016 not found"}"""))),
                    @ApiResponse(responseCode = "406", description = "Невозможно выполнить перевод",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    examples = {@ExampleObject(name = "Недостаточно средств на карте", value = """
                                            {"error": "Insufficient funds on the card to perform the operation"}"""),
                                            @ExampleObject(name = "Карта не активна", value = """
                                                    {"error": "The card **** **** **** 1895 status is not ACTIVE"}""")}
                            ))

            })
    @PostMapping("/transfer")
    public CardBalanceDto transferMoney(@RequestBody @Valid MoneyTransferRequest transferParam,
                                        @RequestHeader(USER_ID_HEADER) @Positive Long userId) {
        transferParam.setUserId(userId);
        return userCardService.cardToCardTransfer(transferParam);
    }
}
