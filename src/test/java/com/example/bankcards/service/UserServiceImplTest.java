package com.example.bankcards.service;

import com.example.bankcards.dto.CardShortDto;
import com.example.bankcards.dto.UserCreateRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserFullDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.UserRole;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private CardRepository cardRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private Long userId = 1L;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setTestData() {
        user = new User();
        user.setName("username");
        user.setEmail("username@mail.ru");
        user.setRole(UserRole.USER);
        user.setId(userId);
        user.setPassword("password123");

        userDto = new UserDto(userId, user.getName(), user.getEmail(), user.getRole());
    }

    @Test
    void createUser_whenValidUser_thenUserCreated() {
        UserCreateRequest request = new UserCreateRequest(
                "username", "username@mail.ru", UserRole.USER, "password123");

        when(userMapper.toEntity(request)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(userRepository.save(user)).thenReturn(user);

        UserDto actual = userService.createUser(request);
        verify(userRepository).save(user);
        assertEquals(userDto, actual);
    }

    @Test
    void getUser_whenUserExists_thenUserFullDtoReturned() {
        Card card = new Card();
        card.setNumber("1234567890123456");
        card.setOwner(user);
        card.setStatus(CardStatus.BLOCKED);
        card.setExpiration(card.getCreated().plusYears(1L).toLocalDate());
        card.setBalance(BigDecimal.valueOf(1000));
        List<Card> cards = new ArrayList<>(List.of(card));

        UserFullDto expected = UserFullDto.builder()
                .id(userId)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .cards(List.of(CardShortDto.builder()
                        .number(card.getNumber())
                        .status(card.getStatus())
                        .balance(card.getBalance())
                        .created(card.getCreated())
                        .expiration(card.getExpiration())
                        .build()))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.findAllByOwnerId(userId)).thenReturn(cards);
        when(userMapper.toFullDto(user, cards)).thenReturn(expected);

        UserFullDto actual = userService.getUser(userId);

        assertEquals(expected, actual);
    }

    @Test
    void getUser_whenUserNotExists_thenThrowNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUser(userId));
        verify(userRepository).findById(userId);
    }

}