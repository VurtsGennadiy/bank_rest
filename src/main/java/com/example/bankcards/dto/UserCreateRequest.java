package com.example.bankcards.dto;

import com.example.bankcards.entity.UserRole;
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
public class UserCreateRequest {
    @NotBlank
    @Size(min = 2, max = 255)
    String name;

    @NotBlank
    @Email
    String email;

    @NotNull
    UserRole role;

    @NotNull
    @Size(min = 8)
    String password;
}
