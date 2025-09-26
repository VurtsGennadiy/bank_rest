package com.example.bankcards.util;

import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserFullDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import org.mapstruct.*;

import java.util.List;

/**
 * Маппинг между сущностями и DTO для user
 */

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);

    @Mapping(target = "id", ignore = true)
    User toEntity(CreateUserRequest dto);

    UserFullDto toFullDto(User user, List<Card> cards);
}
