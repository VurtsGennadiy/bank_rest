package com.example.bankcards.controller.adminAPI;

import com.example.bankcards.config.SecurityTestConfig;
import com.example.bankcards.dto.UserCreateRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserFullDto;
import com.example.bankcards.dto.filters.UserSearchParam;
import com.example.bankcards.entity.UserRole;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityTestConfig.class)
@WebMvcTest(AdminUserController.class)
@AutoConfigureMockMvc
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    void createUser() {
        UserCreateRequest userRequest = UserCreateRequest.builder()
                .username("Ivan Ivanov")
                .email("ivan@mail.ru")
                .role(UserRole.USER)
                .password("qwerty123")
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("Ivan Ivanov")
                .email("ivan@mail.ru")
                .role(UserRole.USER)
                .build();

        when(userService.createUser(userRequest)).thenReturn(userDto);

        String response = mockMvc.perform(post("/admin/users")
                        .content(objectMapper.writeValueAsString(userRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), response);
        verify(userService, times(1)).createUser(userRequest);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    void createUser_whenUserNotValid_thenReturnBadRequest() {
        UserCreateRequest userRequest = UserCreateRequest.builder()
                .username("Ivan Ivanov")
                .email("ivan@mail.ru")
                .role(UserRole.USER)
                .password("")
                .build();

        mockMvc.perform(post("/admin/users")
                        .content(objectMapper.writeValueAsString(userRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON));

        verify(userService, never()).createUser(userRequest);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    void getUsers() {
        mockMvc.perform(get("/admin/users")
                        .param("username", "ivan")
                        .param("email", "@mail"))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON));

        verify(userService, times(1))
                .getUsers(new UserSearchParam("ivan", "@mail", 0, 20));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    void getUser() {
        Long userId = 1L;
        UserFullDto userDto = UserFullDto.builder()
                .id(userId)
                .role(UserRole.USER)
                .email("ivan@mail.ru")
                .username("Ivan Ivanov")
                .build();

        when(userService.getUser(userId)).thenReturn(userDto);

        String response = mockMvc.perform(get("/admin/users/{userId}", userId))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService, times(1)).getUser(userId);
        assertEquals(objectMapper.writeValueAsString(userDto), response);
    }

    @Test
    @WithMockUser(roles = "USER")
    @SneakyThrows
    void createUser_whenUnauthenticated_thenReturnStatus403() {
        mockMvc.perform(post("/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @SneakyThrows
    void getUsers_whenUnauthenticated_thenReturnStatus403() {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @SneakyThrows
    void getUser_whenUnauthenticated_thenReturnStatus403() {
        mockMvc.perform(get("/admin/users/{userId}", 1L))
                .andExpect(status().isForbidden());
    }
}