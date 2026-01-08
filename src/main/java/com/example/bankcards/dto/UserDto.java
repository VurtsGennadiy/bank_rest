package com.example.bankcards.dto;

import com.example.bankcards.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Информация о пользователе")
public class UserDto {
    @Schema(description = "id пользователя", example = "1")
    Long id;

    @Schema(description = "имя пользователя", example = "Иван Иванов")
    String username;

    @Schema(description = "email пользователя", example = "ivan@mail.ru")
    String email;

    @Schema(description = "роль пользователя", example = "ROLE_USER")
    UserRole role;
}
