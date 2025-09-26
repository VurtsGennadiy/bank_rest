package com.example.bankcards.service;

import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserFullDto;
import com.example.bankcards.dto.param.UserSearchParam;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.UserMapper;
import com.example.bankcards.util.UserSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(CreateUserRequest userRequest) {
        log.debug("Request to create new user: {}", userRequest);
        User user = userMapper.toEntity(userRequest);
        user = userRepository.save(user);
        log.info("Created new user id: '{}', name: '{}', email: '{}', role: '{}'",
                user.getId(), user.getName(), user.getEmail(), user.getRole());
        return userMapper.toDto(user);
    }

    /**
     * Получить информацию о пользователе, включающую все его карты
     */
    @Override
    public UserFullDto getUser(Long userId) {
        log.trace("Get user request");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %s not found", userId)));
        List<Card> cards = cardRepository.findAllByUserId(userId);
        return userMapper.toFullDto(user, cards);
    }

    /**
     * Поиск пользователей с поддержкой фильтрации и пагинации
     */
    @Override
    public List<UserDto> getUsers(UserSearchParam params) {
        log.trace("Get users request {}", params);
        Pageable page = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        List<User> users = userRepository.findAll(UserSpecifications.userSearchSpec(params),  page).getContent();
        return userMapper.toDto(users);
    }
}
