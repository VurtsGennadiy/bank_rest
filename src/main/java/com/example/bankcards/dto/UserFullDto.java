package com.example.bankcards.dto;

import com.example.bankcards.entity.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFullDto {
    Long id;

    String name;

    String email;

    UserRole role;

    List<CardShortDto> cards;
}
