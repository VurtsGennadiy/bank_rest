package com.example.bankcards.controller.adminAPI;

import com.example.bankcards.dto.UserCreateRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserFullDto;
import com.example.bankcards.dto.filters.UserSearchParam;
import com.example.bankcards.exception.ErrorResponse;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/users")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Админ: Пользователи",
        description = "API для управления пользователями"
)
@SecurityRequirement(name = "JWT authentication")
public class AdminUserController {
    private final UserService userService;

    @Operation(summary = "Создание нового пользователя", responses = {
            @ApiResponse(responseCode = "201", description = "Пользователь создан",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class,
                            examples = """
                                    {"error": "Поле password размер должен находиться в диапазоне от 8 до 255."}
                                    """)
                    ))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserCreateRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @Operation(summary = "Поиск пользователей", description = "Поддерживается фильтрация и пагинация",
            parameters = {
                    @Parameter(name = "username", description = "Имя пользователя, поддерживается поиск по части имени", example = "Иван"),
                    @Parameter(name = "email", description = "Email пользователя, поддерживается поиск по части email", example = "@mail"),
                    @Parameter(name = "from", description = "Индекс первого элемента, с которого нужно вернуть"),
                    @Parameter(name = "size", description = "Количество возвращаемых элементов")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователи найдены",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            examples = """
                                                    {"error":"from должно быть не меньше 0."}"""
                                    )))
            })
    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) String username,
                                  @RequestParam(required = false) String email,
                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                  @RequestParam(defaultValue = "20") @Positive Integer size) {
        return userService.getUsers(
                UserSearchParam.builder()
                        .username(username)
                        .email(email)
                        .from(from)
                        .size(size)
                        .build()
        );
    }

    @Operation(summary = "Получение пользователя по его Id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь найден",
                            content = @Content(schema = @Schema(implementation = UserFullDto.class))),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class,
                                    example = """
                                                {"error": "User with id 42 not found"}"""))),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class,
                                    example = """
                                                {"error": "userId должно быть больше 0."}""")))
            })
    @GetMapping("/{userId}")
    public UserFullDto getUser(
            @Parameter(description = "Id пользователя", example = "1")
            @PathVariable @Positive Long userId) {
        return userService.getUser(userId);
    }
}
