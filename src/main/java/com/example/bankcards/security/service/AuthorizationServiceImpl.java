package com.example.bankcards.security.service;

import com.example.bankcards.security.JwtProvider;
import com.example.bankcards.security.dto.JwtRequest;
import com.example.bankcards.security.dto.JwtResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationServiceImpl implements AuthorizationService {
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public JwtResponse authorize(JwtRequest authRequest) {
        Authentication authToken = UsernamePasswordAuthenticationToken.unauthenticated(
                authRequest.getUsername(), authRequest.getPassword());
        authenticationManager.authenticate(authToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        String jwt = jwtProvider.generateToken(userDetails);
        log.info("User {} authorized", userDetails.getUsername());
        return new JwtResponse(jwt);
    }
}
