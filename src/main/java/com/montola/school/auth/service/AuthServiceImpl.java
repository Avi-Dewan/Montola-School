package com.montola.school.auth.service;

import com.montola.school.auth.dto.LoginRequest;
import com.montola.school.auth.dto.AuthResponse;
import com.montola.school.auth.enums.Role;
import com.montola.school.auth.model.RefreshToken;
import com.montola.school.auth.model.User;
import com.montola.school.auth.repository.UserRepository;
import com.montola.school.common.exception.InvalidCredentialsException;
import com.montola.school.common.exception.UserNotActivatedException;
import com.montola.school.common.exception.UserNotFoundException;
import com.montola.school.common.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author avidewan
 * @date 8/29/25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.email());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(UserNotFoundException::new);

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

        } catch (BadCredentialsException ex) {
            log.warn("Invalid credentials for email: {}", request.email());

            throw new InvalidCredentialsException();

        }

        if (!user.getIsActivated()) {
            throw new UserNotActivatedException();
        }

        String accessToken = jwtService.generateToken(
                user.getEmail(),
                Map.of( "roles", user.getRoles())
        );
        
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        
        log.info("Generated JWT and Refresh Token for email: {}", user.getEmail());

        Set<String> roles = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet());

        return new AuthResponse(accessToken, refreshToken.getToken(), user.getEmail(), user.getFullName(), roles);
    }

    @Override
    public void logout(Long userId) {
        log.info("Logout requested for user ID: {}", userId);
        refreshTokenService.deleteByUserId(userId);
        log.info("Logout successful for user ID: {}", userId);
    }
}