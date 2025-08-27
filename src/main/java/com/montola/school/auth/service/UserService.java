package com.montola.school.auth.service;

import com.montola.school.auth.dto.UserRegisterRequest;
import com.montola.school.auth.enums.Role;
import com.montola.school.auth.model.User;

import java.util.Optional;
import java.util.Set;

/**
 * @author avidewan
 * @date 8/27/25
 */
public interface UserService {

    User createUser(UserRegisterRequest request);

    Optional<User> findByEmail(String email);

    boolean emailExists(String email);
}