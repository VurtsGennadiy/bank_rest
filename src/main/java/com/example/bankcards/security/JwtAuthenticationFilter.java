package com.example.bankcards.security;

import com.example.bankcards.entity.UserRole;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = getTokenFromRequest(request);
        if (jwt != null) {
            String userId;
            List<UserRole> roles;
            try {
                Claims claims = jwtProvider.getClaims(jwt);
                userId = claims.getSubject();

                List<?> rawRoles = claims.get("roles", List.class);
                roles = rawRoles.stream()
                        .map(Object::toString)
                        .map(UserRole::valueOf)
                        .toList();

                UsernamePasswordAuthenticationToken authToken =
                        UsernamePasswordAuthenticationToken.authenticated(Long.valueOf(userId), null, roles);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid JWT signature", e);
            }

        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
       String authHeader = request.getHeader("Authorization");
       if (authHeader != null && authHeader.startsWith("Bearer ")) {
           authHeader = authHeader.substring(7);
       }
       return authHeader;
    }
}
