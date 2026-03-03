package com.montola.school.auth.service;

import com.montola.school.auth.dto.RefreshTokenRequest;
import com.montola.school.auth.dto.RefreshTokenResponse;
import com.montola.school.auth.model.RefreshToken;
import com.montola.school.auth.model.User;

import java.util.Optional;

/**
 * Service for managing refresh tokens.
 *
 * @author avidewan
 * @date 12/15/2025
 */
public interface RefreshTokenService {

    /**
     * Creates a new refresh token for the given user.
     *
     * @param userId the user ID
     * @return the created refresh token
     */
    RefreshToken createRefreshToken(Long userId);

    /**
     * Verifies the expiration of the refresh token.
     *
     * @param token the refresh token entity
     * @return the verified refresh token
     */
    RefreshToken verifyExpiration(RefreshToken token);

    /**
     * Refreshes the access token using the refresh token.
     *
     * @param request the refresh token request
     * @return the new access token and refresh token
     */
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    /**
     * Deletes the refresh token for the given user.
     *
     * @param userId the user ID
     */
    void deleteByUserId(Long userId);
}
