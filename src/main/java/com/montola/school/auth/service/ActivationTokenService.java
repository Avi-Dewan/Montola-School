package com.montola.school.auth.service;

import com.montola.school.auth.model.ActivationToken;
import com.montola.school.auth.model.User;
import com.montola.school.auth.repository.ActivationTokenRepository;
import com.montola.school.common.exception.RegistrationTokenExpiredException;
import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.common.service.BusinessEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author avidewan
 * @date 9/8/25
 */
@Service
@RequiredArgsConstructor
public class ActivationTokenService {

    private final ActivationTokenRepository activationTokenRepository;

    private final BusinessEmailService emailService;

    @Transactional
    public void replaceTokenForUser(User user) {
        activationTokenRepository.deleteByUserEmail(user.getEmail());

        issueTokenForUser(user);
    }

    @Transactional
    public void issueTokenForUser(User user) {
        ActivationToken token = buildToken(user);

        activationTokenRepository.save(token);

        emailService.sendActivationEmail(user.getEmail(), token.getToken());
    }

    @Transactional(readOnly = true)
    public ActivationToken findByEmailAndToken(String email, String token) {

        return activationTokenRepository.findByUserEmailAndToken(email, token)
                .orElseThrow(() -> new ResourceNotFoundException("registration.token.notfound"));
    }

    @Transactional
    public void deleteByUserEmail(String email) {
        activationTokenRepository.deleteByUserEmail(email);
    }

    public void validateToken(ActivationToken token) {
        if (token.getExpiry().isBefore(LocalDateTime.now())) {
            throw new RegistrationTokenExpiredException();
        }
    }

    private ActivationToken buildToken(User user) {
        return ActivationToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiry(LocalDateTime.now().plusMinutes(15))
                .build();
    }
}