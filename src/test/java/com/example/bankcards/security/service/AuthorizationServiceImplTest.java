package com.example.bankcards.security.service;

import com.example.bankcards.BankRest;
import com.example.bankcards.security.JwtProvider;
import com.example.bankcards.security.dto.JwtRequest;
import com.example.bankcards.security.dto.JwtResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = BankRest.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class AuthorizationServiceImplTest {

    @Autowired
    private AuthorizationServiceImpl authorizationService;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    void authorize_whenValidUserCredentials_thenJwtTokenGenerated() {
        JwtRequest authRequest = new JwtRequest("admin", "admin");

        JwtResponse response = authorizationService.authorize(authRequest);
        String jwt = response.getJwt();

        assertNotNull(jwt);
        assertNotNull(jwtProvider.getClaims(jwt));
    }

    @Test
    void authorize_whenNotValidUserCredentials_thenThrowBadCredentialsException() {
        JwtRequest authRequest = new JwtRequest("admin", "not_valid_password");

        assertThrows(BadCredentialsException.class, () -> authorizationService.authorize(authRequest));
    }

    @Test
    void authorize_whenUsernameNotFound_thenThrowBadCredentialsException() {
        JwtRequest authRequest = new JwtRequest("not_existing_user", "admin");

        assertThrows(BadCredentialsException.class, () -> authorizationService.authorize(authRequest));
    }
}
