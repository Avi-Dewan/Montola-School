package com.montola.school.auth.service;

import com.montola.school.auth.dto.LoginRequest;
import com.montola.school.auth.dto.AuthResponse;
import com.montola.school.auth.model.User;
import com.montola.school.auth.repository.UserRepository;
import com.montola.school.common.exception.InvalidCredentialsException;
import com.montola.school.common.exception.UserNotFoundException;
import com.montola.school.common.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author avidewan
 * @date 8/29/25
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(UserNotFoundException::new);

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException();

        }

        String token = jwtService.generateToken(
                user.getEmail(),
                Map.of("roles", user.getRoles())
        );

        return new AuthResponse(token, user.getEmail());
    }
}