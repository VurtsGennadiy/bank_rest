package com.example.bankcards.controller.adminAPI;

import com.example.bankcards.dto.UserCreateRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.filters.UserSearchParam;
import com.example.bankcards.entity.UserRole;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    }

    @Test
    @SneakyThrows
    void createUser_whenUserNotValid_thenReturnBadRequest() {
        UserCreateRequest userRequest = UserCreateRequest.builder()
                .username("Ivan Ivanov")
                .email("ivan@mail.ru")
                .role(UserRole.USER)
                .password("")
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("Ivan Ivanov")
                .email("ivan@mail.ru")
                .role(UserRole.USER)
                .build();

        when(userService.createUser(userRequest)).thenReturn(userDto);

        mockMvc.perform(post("/admin/users")
                        .content(objectMapper.writeValueAsString(userRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON));


        verify(userService, never()).createUser(userRequest);
    }

    @Test
    @SneakyThrows
    void getUsers() {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .getUsers(new UserSearchParam(null, null, 0, 20));
    }

    @Test
    @SneakyThrows
    void getUser() {
        Long userId = 1L;

        mockMvc.perform(get("/admin/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUser(userId);
    }
}