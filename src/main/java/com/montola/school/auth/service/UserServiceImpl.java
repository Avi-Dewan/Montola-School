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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ActivationTokenService activationTokenService;
    private final ResetPasswordTokenService resetPasswordTokenService;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(UserRegisterRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .map(user -> handleExistingUnactivatedUser(user, request))
                .orElseGet(() -> handleNewUser(request));
    }

    @Override
    @Transactional
    public void activateUser(String email, String token) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("registration.token.notfound"));

        if (user.getIsActivated()) {
            throw new ResourceAlreadyExistsException("user.already.activated");
        }

        ActivationToken activation = activationTokenService.findByEmailAndToken(email, token);
        activationTokenService.validateToken(activation);

        user.setIsActivated(true);
        userRepository.save(user);

        activationTokenService.deleteByUserEmail(email);
    }

    @Override
    @Transactional
    public void resendActivationToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        if (user.getIsActivated()) {
            throw new ResourceAlreadyExistsException("user.already.activated");
        }

        activationTokenService.replaceTokenForUser(user);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        CustomUserDetails currentUser =
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound"));

        resetPasswordTokenService.issueTokenForUser(user);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound"));

        ResetPasswordToken passwordToken = resetPasswordTokenService.
                findByEmailAndToken(request.getEmail(),
                        request.getToken());
        resetPasswordTokenService.validateToken(passwordToken);

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
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
        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User saved = userRepository.save(user);

        activationTokenService.issueTokenForUser(saved);
        return saved;
    }

    private User handleExistingUnactivatedUser(User existingUser, UserRegisterRequest request) {
        if (existingUser.getIsActivated()) {
            throw new ResourceAlreadyExistsException("user.already.exists");
        }

        existingUser.setEmail(request.getPhone());
        existingUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        existingUser.setRoles(request.getRoles());

        User updated = userRepository.save(existingUser);

        activationTokenService.replaceTokenForUser(updated);

        return updated;
    }
}