package com.montola.school.auth.service;

import com.montola.school.auth.dto.ChangePasswordRequest;
import com.montola.school.auth.dto.ResetPasswordRequest;
import com.montola.school.auth.dto.UserRegisterRequest;
import com.montola.school.auth.enums.Role;
import com.montola.school.auth.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author avidewan
 * @date 8/27/25
 */
public interface UserService {

    User createUser(UserRegisterRequest request);

    void activateUser(String email, String token);

    void resendActivationToken(String email);

    void changePassword(ChangePasswordRequest request);

    void requestPasswordReset(String email);

    void resetPassword(ResetPasswordRequest request);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    boolean emailExists(String email);

    List<User> findAllByRolesContaining(Role role);

    List<User> findAll();
}