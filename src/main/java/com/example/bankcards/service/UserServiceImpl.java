package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreateRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserFullDto;
import com.example.bankcards.dto.filters.UserSearchParam;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.mapper.UserMapper;
import com.example.bankcards.util.specification.UserSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDto createUser(UserCreateRequest userRequest) {
        log.debug("Request to create new user: {}", userRequest);
        User user = userMapper.toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        log.info("Created new user: {}", user);
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
        List<Card> cards = cardRepository.findAllByOwnerId(userId);
        return userMapper.toFullDto(user, cards);
    }

    /**
     * Поиск пользователей с поддержкой фильтрации и пагинации
     */
    @Override
    public List<UserDto> getUsers(UserSearchParam params) {
        log.trace("Get users request {}", params);
        Pageable page = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        List<User> users = userRepository.findAll(UserSpecifications.userSearchSpec(params), page).getContent();
        return userMapper.toDto(users);
    }
}
