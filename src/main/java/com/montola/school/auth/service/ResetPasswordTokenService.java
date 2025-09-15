package com.montola.school.auth.service;

import com.montola.school.auth.model.ResetPasswordToken;
import com.montola.school.auth.model.User;
import com.montola.school.auth.repository.ResetPasswordTokenRepository;
import com.montola.school.common.exception.RegistrationTokenExpiredException;
import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.common.exception.TokenExpiredException;
import com.montola.school.common.service.BusinessEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service responsible for issuing, validating, and managing password reset tokens.
 * <p>
 * Tokens are short-lived (15 minutes) and sent via email to the user.
 *
 * @author avidewan
 * @date 9/10/25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordTokenService {

    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final BusinessEmailService emailService;

    /**
     * Issues a new password reset token and sends it to the user's email.
     *
     * @param user the user to issue the reset token for
     */
    @Transactional
    public void issueTokenForUser(User user) {
        ResetPasswordToken token = buildToken(user);
        resetPasswordTokenRepository.save(token);

        log.info("Issued password reset token for user {}", user.getEmail());
        emailService.sendPasswordResetEmail(user.getEmail(), token.getToken());
    }

    /**
     * Finds a reset token by email and token string.
     *
     * @param email user's email
     * @param token token string
     * @return ResetPasswordToken if found
     * @throws ResourceNotFoundException if no matching token is found
     */
    @Transactional(readOnly = true)
    public ResetPasswordToken findByEmailAndToken(String email, String token) {
        log.debug("Looking up reset token for email {}", email);

        return resetPasswordTokenRepository.findByUserEmailAndToken(email, token)
                .orElseThrow(() -> {
                    log.warn("Reset token not found for email {}", email);

                    return new ResourceNotFoundException("reset.token.notfound");
                });
    }

    /**
     * Deletes reset tokens for the given email.
     *
     * @param email user's email
     */
    @Transactional
    public void deleteByUserEmail(String email) {
        log.info("Deleting reset token for email {}", email);

        resetPasswordTokenRepository.deleteByUserEmail(email);
    }

    /**
     * Validates that the token is not expired.
     *
     * @param token the reset password token to validate
     * @throws TokenExpiredException if the token is expired
     */
    public void validateToken(ResetPasswordToken token) {
        if (token.getExpiry().isBefore(LocalDateTime.now())) {
            log.warn("Reset token expired for user {}", token.getUser().getEmail());

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