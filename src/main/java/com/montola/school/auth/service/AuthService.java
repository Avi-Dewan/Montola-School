package com.montola.school.auth.service;

import com.montola.school.auth.dto.AuthResponse;
import com.montola.school.auth.dto.LoginRequest;

/**
 * Service that handles authentication-related operations.
 *
 * Provides functionality such as login and token generation.
 *
 * @author avidewan
 * @date 8/29/25
 */
public interface AuthService {

    /**
     * Handles login by validating credentials and generating a JWT token.
     *
     * @param loginRequest contains the email and password.
     * @return authentication response with JWT and user email.
     * @throws com.montola.school.common.exception.UserNotFoundException
     *              if no user exists with the given email.
     * @throws com.montola.school.common.exception.InvalidCredentialsException
     *              if the password is invalid.
     * @throws com.montola.school.common.exception.UserNotActivatedException
     *              if the user exists but is not activated.
     */
    public AuthResponse login(LoginRequest loginRequest);
}