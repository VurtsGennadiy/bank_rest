package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreateRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserFullDto;
import com.example.bankcards.dto.filters.UserSearchParam;

import java.util.List;

public interface UserService {
    UserDto createUser(UserCreateRequest userRequest);

    UserFullDto getUser(Long userId);

    List<UserDto> getUsers(UserSearchParam params);
}
