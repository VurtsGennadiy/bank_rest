package com.example.bankcards.controller.publicAPI;

import com.example.bankcards.security.service.AuthorizationService;
import com.example.bankcards.security.dto.JwtRequest;
import com.example.bankcards.security.dto.JwtResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthorizationController {
    private final AuthorizationService authorizationService;

    @PostMapping("/auth")
    public JwtResponse authorization(@RequestBody JwtRequest authRequest) {
        return authorizationService.authorize(authRequest);
    }
}
