package com.montola.school.auth.service;

import com.montola.school.auth.model.ResetPasswordToken;
import com.montola.school.auth.model.User;
import com.montola.school.auth.repository.ResetPasswordTokenRepository;
import com.montola.school.common.exception.RegistrationTokenExpiredException;
import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.common.exception.TokenExpiredException;
import com.montola.school.common.service.BusinessEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author avidewan
 * @date 9/10/25
 */
@Service
@RequiredArgsConstructor
public class ResetPasswordTokenService {

    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final BusinessEmailService emailService;

    @Transactional
    public void issueTokenForUser(User user) {
        ResetPasswordToken token = buildToken(user);
        resetPasswordTokenRepository.save(token);

        emailService.sendPasswordResetEmail(user.getEmail(), token.getToken());
    }

    @Transactional(readOnly = true)
    public ResetPasswordToken findByEmailAndToken(String email, String token) {
        return resetPasswordTokenRepository.findByUserEmailAndToken(email, token)
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound"));
    }

    @Transactional
    public void deleteByUserEmail(String email) {
        resetPasswordTokenRepository.deleteByUserEmail(email);
    }

    public void validateToken(ResetPasswordToken token) {
        if (token.getExpiry().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("reset.token.expired");
        }
    }

    private ResetPasswordToken buildToken(User user) {
        return ResetPasswordToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiry(LocalDateTime.now().plusMinutes(15))
                .build();
    }
}

