package com.montola.school.auth.service;

import com.montola.school.auth.dto.UserRegisterRequest;
import com.montola.school.auth.enums.Role;
import com.montola.school.auth.mapper.UserMapper;
import com.montola.school.auth.model.ActivationToken;
import com.montola.school.auth.model.User;
import com.montola.school.auth.repository.ActivationTokenRepository;
import com.montola.school.auth.repository.UserRepository;
import com.montola.school.common.exception.ResourceAlreadyExistsException;
import com.montola.school.common.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * @author avidewan
 * @date 8/27/25
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ActivationTokenRepository activationTokenRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
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

        System.out.println("Activation token for " + saved.getEmail() + ": " + token.getToken());

        return saved;
    }

    @Override
    public void activateUser(String email, String token) {
        ActivationToken activation = activationTokenRepository
                .findByUserEmailAndToken(email, token)
                .orElseThrow(UserNotFoundException::new);

        if (activation.getExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Activation token expired");
        }

        User user = activation.getUser();
        user.setIsActivated(true);
        userRepository.save(user);

        activationTokenRepository.deleteByUserEmail(email);
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

