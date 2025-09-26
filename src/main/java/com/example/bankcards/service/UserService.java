package com.example.bankcards.service;

import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserFullDto;
import com.example.bankcards.dto.param.UserSearchParam;

import java.util.List;

public interface UserService {
    UserDto createUser(CreateUserRequest userRequest);

    UserFullDto getUser(Long userId);

    List<UserDto> getUsers(UserSearchParam params);
}
