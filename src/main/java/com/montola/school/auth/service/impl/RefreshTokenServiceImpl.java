package com.montola.school.auth.service.impl;

import com.montola.school.auth.dto.RefreshTokenRequest;
import com.montola.school.auth.dto.RefreshTokenResponse;
import com.montola.school.auth.model.RefreshToken;
import com.montola.school.auth.repository.RefreshTokenRepository;
import com.montola.school.auth.repository.UserRepository;
import com.montola.school.auth.service.RefreshTokenService;
import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.common.security.JwtProperties;
import com.montola.school.common.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of {@link RefreshTokenService}.
 *
 * @author avidewan
 * @date 12/15/2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        log.info("Creating refresh token for user ID: {}", userId);

        // Delete any existing refresh token for the user
        userRepository.findById(userId).ifPresent(refreshTokenRepository::deleteByUser);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
        
        // Default to 7 days if not configured
        int refreshExpirationDays = jwtProperties.refreshExpirationDays() != null ? jwtProperties.refreshExpirationDays() : 7;
        refreshToken.setExpiryDate(Instant.now().plusSeconds(refreshExpirationDays * 24 * 60 * 60));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Override
    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenRepository.findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateToken(user.getEmail(), Map.of("roles", user.getRoles()));
                    
                    // Optionally rotate refresh token here for better security
                    // For now, we keep the same refresh token until it expires
                    
                    return new RefreshTokenResponse(accessToken, requestRefreshToken);
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        userRepository.findById(userId).ifPresent(refreshTokenRepository::deleteByUser);
    }
}
