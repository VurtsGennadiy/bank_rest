package com.example.bankcards.controller.publicAPI;

import com.example.bankcards.security.service.AuthorizationService;
import com.example.bankcards.security.dto.JwtRequest;
import com.example.bankcards.security.dto.JwtResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Авторизация")
public class AuthorizationController {
    private final AuthorizationService authorizationService;

    @Operation(summary = "Логин", description = "Авторизация в системе и получение JWT - токена")
    @PostMapping("/auth")
    public JwtResponse authorization(@RequestBody JwtRequest authRequest) {
        return authorizationService.authorize(authRequest);
    }
}
