package com.example.bankcards.dto.param;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSearchParam {
    String name;

    String email;

    Integer from;

    Integer size;
}
