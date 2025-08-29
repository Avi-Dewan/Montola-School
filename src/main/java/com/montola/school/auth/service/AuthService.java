package com.montola.school.auth.service;

import com.montola.school.auth.dto.AuthResponse;
import com.montola.school.auth.dto.LoginRequest;

/**
 * @author avidewan
 * @date 8/29/25
 */
public interface AuthService {

    public AuthResponse login(LoginRequest loginRequest);
}