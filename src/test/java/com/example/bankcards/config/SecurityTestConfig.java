package com.example.bankcards.config;

import com.example.bankcards.entity.User;
import com.example.bankcards.entity.UserRole;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.SecurityConfig;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.when;

@TestConfiguration
@Import(SecurityConfig.class)
public class SecurityTestConfig {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public UserRepository userRepository() {
        User admin = User.builder()
                .id(1L)
                .username("admin")
                .email("admin@effective.ru")
                .role(UserRole.ADMIN)
                .password(passwordEncoder.encode("admin_password"))
                .build();

        User user = User.builder()
                .id(2L)
                .username("ivan")
                .email("ivan@mail.ru")
                .role(UserRole.USER)
                .password(passwordEncoder.encode("ivan_password"))
                .build();

        var userRepository = Mockito.mock(UserRepository.class);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(userRepository.findByUsername("ivan")).thenReturn(Optional.of(user));
        return userRepository;
    }
}
