package com.example.bankcards.controller;

import com.example.bankcards.config.SecurityTestConfig;
import com.example.bankcards.controller.adminAPI.AdminUserController;
import com.example.bankcards.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityTestConfig.class)
@WebMvcTest(AdminUserController.class)
@AutoConfigureMockMvc
public class AuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @SneakyThrows
    void authentication_whenValidUser_thenOk() {
        mockMvc.perform(get("/admin/users")
                        .with(httpBasic("admin", "admin_password")))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void authentication_whenInvalidUser_thenStatus401() {
        mockMvc.perform(get("/admin/users")
                        .with(httpBasic("admin", "invalid_password")))
                .andExpect(status().isUnauthorized());
    }
}
