package com.example.bankcards.dto;

import com.example.bankcards.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * ДТО с необходимыми параметрами для создания нового пользователя
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Данные для создания нового пользователя")
public class UserCreateRequest {
    @NotBlank
    @Size(min = 2, max = 255)
    @Schema(description = "имя пользователя", example = "Иван Иванов", minLength = 2, maxLength = 255)
    String name;

    @NotBlank
    @Email
    @Schema(description = "email пользователя", example = "ivan@mail.ru")
    String email;

    @NotNull
    @Schema(description = "роль пользователя", example = "USER")
    UserRole role;

    @NotNull
    @Size(min = 8, max = 255)
    @Schema(description = "пароль пользователя", example = "qwerty123", minLength = 8, maxLength = 255)
    String password;
}
