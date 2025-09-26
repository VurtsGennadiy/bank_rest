package com.example.bankcards.controller.adminAPI;

import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserFullDto;
import com.example.bankcards.dto.param.UserSearchParam;
import com.example.bankcards.service.UserServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid CreateUserRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) String name,
                                  @RequestParam(required = false) String email,
                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                  @RequestParam(defaultValue = "20") @Positive Integer size) {

        return userService.getUsers(
                UserSearchParam.builder()
                        .name(name)
                        .email(email)
                        .from(from)
                        .size(size)
                        .build()
        );
    }

    @GetMapping("/{userId}")
    public UserFullDto getUser(@PathVariable @Positive Long userId) {
        return userService.getUser(userId);
    }
}
