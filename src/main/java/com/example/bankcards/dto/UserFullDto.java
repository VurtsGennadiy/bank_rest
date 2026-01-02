package com.example.bankcards.dto;

import com.example.bankcards.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Информация о пользователе, включающая информацию о его картах")
public class UserFullDto {
    @Schema(description = "id пользователя", example = "1")
    Long id;

    @Schema(description = "имя пользователя", example = "Иван Иванов")
    String username;

    @Schema(description = "email пользователя", example = "ivan@mail.ru")
    String email;

    @Schema(description = "роль пользователя", example = "USER")
    UserRole role;

    @Schema(description = "список карт пользователя")
    List<CardShortDto> cards;
}
