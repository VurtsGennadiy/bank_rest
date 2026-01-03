package com.example.bankcards.controller.adminAPI;

import com.example.bankcards.dto.CardBlockingRequestFullDto;
import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardFullDto;
import com.example.bankcards.dto.filters.CardBlockingRequestSearchParam;
import com.example.bankcards.dto.filters.CardSearchParam;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.ErrorResponse;
import com.example.bankcards.exception.ValidationException;
import com.example.bankcards.service.AdminCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/cards")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Админ: Карты",
        description = "API для управления картами пользователей"
)
public class AdminCardController {
    private final AdminCardService adminCardService;
    private final String datePattern = "yyyy-MM-dd";

    @Operation(summary = "Создание новой карты", responses = {
            @ApiResponse(responseCode = "201", description = "Карта создана"),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class,
                            example = """
                                    {"error": "Поле expiration должно содержать дату, которая еще не наступила."}"""))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class,
                            example = """
                                    {"error": "User with id 42 not found"}""")))},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания новой карты. Поле balance опциональное, expiration должно быть в будущем." +
                            " После создания карта имеет статус BLOCKED, нужно её активировать."))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardDto createNewCard(@RequestBody @Valid CardCreateRequest cardRequest) {
        return adminCardService.createNewCard(cardRequest);
    }

    @Operation(summary = "Удаление карты", description = "Запрещено удалять карту имеющую не нулевой баланс",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Карта удалена"),
                    @ApiResponse(responseCode = "404", description = "Карта не найдена",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class,
                                    example = """
                                            {"error": "Card with number **** **** **** 1234 not found"}"""))),
                    @ApiResponse(responseCode = "406", description = "Ошибка удаления карты",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class,
                                    example = """
                                            {"error": "Card with number **** **** **** 1234 has non-zero balance"}""")))},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Номер карты",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class, example = "1234567876543210")))
    )
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@RequestBody String cardNumber) {
        adminCardService.deleteCard(cardNumber.trim());
    }

    @Operation(summary = "Поиск карт", description = "Поддерживается фильтрация и пагинация",
    parameters = {
            @Parameter(name = "number", description = "Номер карты, поддерживается поиск по части номера", example = "1234"),
            @Parameter(name = "userId", description = "ID пользователя", example = "42"),
            @Parameter(name = "status", description = "Статус карты", example = "ACTIVE"),
            @Parameter(name = "created", description = "Дата создания карты", example = "2025-09-28"),
            @Parameter(name = "expiration", description = "Дата окончания срока действия карты", example = "2026-09-28"),
            @Parameter(name = "from", description = "Индекс первого элемента, с которого нужно вернуть"),
            @Parameter(name = "size", description = "Количество возвращаемых элементов")
    },
    responses = {
            @ApiResponse(responseCode = "200", description = "Список найденных карт"),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class,
                            example = """
                                    {"error":"from должно быть не меньше 0."}""")))
    })
    @GetMapping
    public List<CardDto> getCards(@RequestParam(required = false) String number,
                                  @RequestParam(required = false) @Positive Long userId,
                                  @RequestParam(required = false) CardStatus status,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = datePattern) LocalDate created,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = datePattern) LocalDate expiration,
                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                  @RequestParam(defaultValue = "20") @Positive Integer size) {

        return adminCardService.getCards(
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
    @Operation(summary = "Получение информации по карте",
            description = "Основная информация о карте, включая описание владельца. Использую POST метод для получения " +
                    "данных по карте, для того чтобы не передавать чувствительную информацию в параметрах запроса",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Номер карты",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class, example = "1234567876543210"))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Карта найдена"),
                    @ApiResponse(responseCode = "404", description = "Карта не найдена",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class,
                                    example = """
                                            {"error": "Card with number **** **** **** 1234 not found"}""")))
            })
    @PostMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    public CardFullDto getCard(@RequestBody String cardNumber) {
        return adminCardService.getCard(cardNumber.trim());
    }

    @Operation(summary = "Активация карты",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Номер карты",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class, example = "1234567876543210"))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Карта активирована"),
                    @ApiResponse(responseCode = "404", description = "Карта не найдена",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class,
                                    example = """
                                            {"error": "Card with number **** **** **** 1234 not found"}""")))
            }
    )
    @PutMapping("/activate")
    @ResponseStatus(HttpStatus.OK)
    public CardDto activateCard(@RequestBody String cardNumber) {
        return adminCardService.activateCard(cardNumber.trim());
    }

    @Operation(summary = "Блокировка карты",
            description = """
                    Поддерживается блокировка как по запросу от пользователя, так и без него.
                    Для блокировки по запросу пользователя - передать в параметрах Id запроса на блокировку.
                    Для блокировки без запроса от пользователя - указать в теле запроса номер карты, в параметрах запроса при этом ничего не указывать.""",
            parameters = @Parameter(name = "requestId", description = "Номер запроса на блокировку", example = "1"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Номер карты",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class, example = "1234567876543210"))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Карта заблокирована",
                            content = @Content(schema = @Schema(implementation = CardDto.class,
                                    example = """
                                            {
                                              "number": "**** **** **** 1234",
                                              "ownerId": 1,
                                              "created": "2025-09-28T12:00",
                                              "expiration": "2026-09-28",
                                              "status": "BLOCKED",
                                              "balance": 1234.56
                                            }"""))),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class,
                                    example = """
                                            {"error": "Необходимо указать id запроса на блокировку либо номер карты"}"""))),
                    @ApiResponse(responseCode = "404", description = "Не найдена карта или не найден запрос на блокировку",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    examples = {@ExampleObject(name = "Не найдена карта", value = """
                                            {"error": "Card with number **** **** **** 1234 not found"}"""),
                                            @ExampleObject(name = "Не найден запрос на блокировку", value = """
                                                    {"error": "Card blocking request with id 42 not found"}""")}
                            ))
            })
    @PutMapping("/block")
    @ResponseStatus(HttpStatus.OK)
    public CardDto blockCard(@RequestBody(required = false) String cardNumber,
                             @RequestParam(required = false) @Positive Long requestId) {
        if (requestId != null) {
            return adminCardService.blockCardByRequest(requestId);
        } else if (cardNumber != null) {
            return adminCardService.blockCardByNumber(cardNumber.trim());
        } else {
            throw new ValidationException("Необходимо указать id запроса на блокировку либо номер карты");
        }
    }

    @Operation(summary = "Получение запросов на блокировку карт",
            description = "Поддерживается фильтрация и пагинация",
            parameters = {
                    @Parameter(name = "solved", description = "Статус решения запроса", example = "false"),
                    @Parameter(name = "createdFrom", description = "Дата создания запроса с", example = "2025-09-28"),
                    @Parameter(name = "createdTo", description = "Дата создания запроса по", example = "2025-09-29"),
                    @Parameter(name = "from", description = "Индекс первого элемента, с которого нужно вернуть", example = "0"),
                    @Parameter(name = "size", description = "Количество элементов на странице", example = "20")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список найденных запросов на блокировку карт"),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class,
                                    example = """
                                            {"error": "from должно быть не меньше 0."}""")))
            }
    )
    @GetMapping("/blocking-request")
    public List<CardBlockingRequestFullDto> getBlockingRequests(@RequestParam(required = false) Boolean solved,
                                                                @RequestParam(required = false) @DateTimeFormat(pattern = datePattern) LocalDate createdFrom,
                                                                @RequestParam(required = false) @DateTimeFormat(pattern = datePattern) LocalDate createdTo,
                                                                @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                                @RequestParam(defaultValue = "20") @Positive Integer size) {
        return adminCardService.getCardBlockingRequests(
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
