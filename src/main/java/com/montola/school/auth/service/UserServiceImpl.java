package com.montola.school.auth.service;

import com.montola.school.auth.dto.UserRegisterRequest;
import com.montola.school.auth.enums.Role;
import com.montola.school.auth.mapper.UserMapper;
import com.montola.school.auth.model.ActivationToken;
import com.montola.school.auth.model.User;
import com.montola.school.auth.repository.ActivationTokenRepository;
import com.montola.school.auth.repository.UserRepository;
import com.montola.school.common.exception.RegistrationTokenExpiredException;
import com.montola.school.common.exception.ResourceAlreadyExistsException;
import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.common.exception.UserNotFoundException;
import com.montola.school.common.service.BusinessEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author avidewan
 * @date 8/27/25
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ActivationTokenRepository activationTokenRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final BusinessEmailService  businessEmailService;

    @Override
    @Transactional
    public User createUser(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("user.already.exists");
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User saved = userRepository.save(user);

        ActivationToken token = ActivationToken.builder()
                .user(saved)
                .token(UUID.randomUUID().toString())
                .expiry(LocalDateTime.now().plusMinutes(15))
                .build();

        activationTokenRepository.save(token);

        businessEmailService.sendActivationEmail(user.getEmail(), token.getToken());

        return saved;
    }

    @Override
    @Transactional
    public void activateUser(String email, String token) {
        ActivationToken activation = activationTokenRepository
                .findByUserEmailAndToken(email, token)
                .orElseThrow(() -> {
                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new ResourceNotFoundException("registration.token.notfound"));

                    if (user.getIsActivated()) {
                        throw new ResourceAlreadyExistsException("user.already.activated");
                    }

                    return new ResourceNotFoundException("registration.token.notfound");
                });

        if (activation.getExpiry().isBefore(LocalDateTime.now())) {
            throw new RegistrationTokenExpiredException();
        }

        User user = activation.getUser();

        if (user.getIsActivated()) {
            throw new ResourceAlreadyExistsException("user.already.activated");
        }

        user.setIsActivated(true);
        userRepository.save(user);

        activationTokenRepository.deleteByUserEmail(email);
    }


    @Override
    @Transactional
    public void resendActivationToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        if (user.getIsActivated()) {
            throw new ResourceAlreadyExistsException("user.already.activated");
        }

        activationTokenRepository.deleteByUserEmail(email);

        ActivationToken token = ActivationToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiry(LocalDateTime.now().plusMinutes(15))
                .build();

        activationTokenRepository.save(token);

        System.out.println("Resent activation token for " + user.getEmail() + ": " + token.getToken());
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
}

