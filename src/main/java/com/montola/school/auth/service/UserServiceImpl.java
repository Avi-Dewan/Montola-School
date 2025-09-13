package com.montola.school.auth.service;

import com.montola.school.auth.dto.ChangePasswordRequest;
import com.montola.school.auth.dto.ResetPasswordRequest;
import com.montola.school.auth.dto.UserRegisterRequest;
import com.montola.school.auth.enums.Role;
import com.montola.school.auth.mapper.UserMapper;
import com.montola.school.auth.model.ActivationToken;
import com.montola.school.auth.model.ResetPasswordToken;
import com.montola.school.auth.model.User;
import com.montola.school.auth.repository.UserRepository;
import com.montola.school.auth.security.CustomUserDetails;
import com.montola.school.common.exception.InvalidCredentialsException;
import com.montola.school.common.exception.ResourceAlreadyExistsException;
import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.common.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author avidewan
 * @date 8/27/25
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ActivationTokenService activationTokenService;
    private final ResetPasswordTokenService resetPasswordTokenService;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(UserRegisterRequest request) {
        log.info("Attempting to create user with email {}", request.getEmail());

        return userRepository.findByEmail(request.getEmail())
                .map(user -> {
                    log.info("Existing unactivated user found with email {}, updating", user.getEmail());

                    return handleExistingUnactivatedUser(user, request);
                })
                .orElseGet(() -> {
                    log.info("No existing user found, creating new user with email {}", request.getEmail());

                    return handleNewUser(request);
                });
    }

    @Override
    @Transactional
    public void activateUser(String email, String token) {
        log.info("Activating user with email {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Activation failed: user not found for email {}", email);

                    return new ResourceNotFoundException("registration.token.notfound");
                });

        if (user.getIsActivated()) {
            log.warn("User {} is already activated", email);

            throw new ResourceAlreadyExistsException("user.already.activated");
        }

        ActivationToken activation = activationTokenService.findByEmailAndToken(email, token);
        activationTokenService.validateToken(activation);

        user.setIsActivated(true);
        userRepository.save(user);

        activationTokenService.deleteByUserEmail(email);
        log.info("User {} activated successfully", email);
    }

    @Override
    @Transactional
    public void resendActivationToken(String email) {
        log.info("Resending activation token to {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Resend activation failed: user not found for email {}", email);

                    return new UserNotFoundException();
                });

        if (user.getIsActivated()) {
            log.warn("User {} already activated, cannot resend token", email);

            throw new ResourceAlreadyExistsException("user.already.activated");
        }

        activationTokenService.replaceTokenForUser(user);
        log.info("Activation token resent successfully to {}", email);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        CustomUserDetails currentUser =
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("User {} requested password change", currentUser.getUsername());

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> {
                    log.error("Password change failed: user not found with ID {}", currentUser.getId());

                    return new ResourceNotFoundException("user.notfound");
                });

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            log.warn("Invalid old password provided for user {}", currentUser.getUsername());

            throw new InvalidCredentialsException();
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed successfully for user {}", currentUser.getUsername());
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        log.info("Password reset requested for email {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Password reset failed: user not found for email {}", email);

                    return new ResourceNotFoundException("user.notfound");
                });

        resetPasswordTokenService.issueTokenForUser(user);
        log.info("Password reset token issued for user {}", email);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        log.info("Resetting password for email {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Password reset failed: user not found for email {}", request.getEmail());
                    return new ResourceNotFoundException("user.notfound");
                });

        ResetPasswordToken passwordToken = resetPasswordTokenService.
                findByEmailAndToken(request.getEmail(),
                        request.getToken());
        resetPasswordTokenService.validateToken(passwordToken);

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        resetPasswordTokenService.deleteByUserEmail(user.getEmail());
        log.info("Password reset successfully for user {}", request.getEmail());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<User> findAllByRolesContaining(Role role) {
        return userRepository.findAllByRolesContaining(role);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    private User handleNewUser(UserRegisterRequest request) {
        log.info("Creating new user entity for email {}", request.getEmail());
        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User saved = userRepository.save(user);

        activationTokenService.issueTokenForUser(saved);
        log.info("New user created and activation token issued for {}", saved.getEmail());

        return saved;
    }

    private User handleExistingUnactivatedUser(User existingUser, UserRegisterRequest request) {
        log.info("Updating existing unactivated user {}", existingUser.getEmail());

        if (existingUser.getIsActivated()) {
            log.warn("Cannot update user {}: already activated", existingUser.getEmail());

            throw new ResourceAlreadyExistsException("user.already.exists");
        }

        existingUser.setEmail(request.getPhone());
        existingUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        existingUser.setRoles(request.getRoles());

        User updated = userRepository.save(existingUser);

        activationTokenService.replaceTokenForUser(updated);
        log.info("Existing unactivated user updated and new activation token issued for {}", updated.getEmail());

        return updated;
    }
}