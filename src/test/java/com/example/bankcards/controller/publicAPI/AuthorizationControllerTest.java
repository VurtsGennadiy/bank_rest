package com.example.bankcards.controller.publicAPI;

import com.example.bankcards.config.SecurityTestConfig;
import com.example.bankcards.security.dto.JwtRequest;
import com.example.bankcards.security.dto.JwtResponse;
import com.example.bankcards.security.service.AuthorizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorizationController.class)
@Import(SecurityTestConfig.class)
class AuthorizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthorizationService authorizationService;

    @Test
    @SneakyThrows
    void authorization_whenUserValid_thenStatusOkAndJwtReturned() {
        JwtResponse expected = new JwtResponse("expected.token");

        when(authorizationService.authorize(any())).thenReturn(expected);

        JwtRequest request = new JwtRequest("admin", "admin_password");
        String response = mockMvc.perform(post("/auth")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(authorizationService).authorize(request);
        assertEquals(objectMapper.writeValueAsString(expected),response);
    }

    @Test
    @SneakyThrows
    void authorization_whenUserNotValid_thenStatus401AndErrorReturned() {
        String errorMessage = "Invalid username or password";
        when(authorizationService.authorize(any())).thenThrow(new BadCredentialsException(errorMessage));

        JwtRequest request = new JwtRequest("admin", "not_valid_password");
        mockMvc.perform(post("/auth")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isUnauthorized(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.error").value(errorMessage),
                        jsonPath("$.jwt").doesNotExist());

        verify(authorizationService).authorize(request);
    }
}