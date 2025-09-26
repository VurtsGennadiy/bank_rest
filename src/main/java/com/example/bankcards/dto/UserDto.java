package com.example.bankcards.dto;

import com.example.bankcards.entity.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;

    String name;

    String email;

    UserRole role;
}
