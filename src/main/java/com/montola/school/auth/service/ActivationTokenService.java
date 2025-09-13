package com.montola.school.auth.service;

import com.montola.school.auth.model.ActivationToken;
import com.montola.school.auth.model.User;
import com.montola.school.auth.repository.ActivationTokenRepository;
import com.montola.school.common.exception.RegistrationTokenExpiredException;
import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.common.service.BusinessEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service responsible for issuing, validating, and managing user activation tokens.
 * <p>
 * Tokens are short-lived (15 minutes) and sent via email to the user.
 *
 * @author avidewan
 * @date 9/8/25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivationTokenService {

    private final ActivationTokenRepository activationTokenRepository;

    private final BusinessEmailService emailService;

    /**
     * Deletes any existing activation token for the user and issues a new one.
     *
     * @param user the user to refresh the activation token for
     */
    @Transactional
    public void replaceTokenForUser(User user) {
        log.info("Replacing activation token for user {}", user.getEmail());
        activationTokenRepository.deleteByUserEmail(user.getEmail());

        issueTokenForUser(user);
    }

    /**
     * Issues a new activation token and sends it to the user's email.
     *
     * @param user the user to issue the token for
     */
    @Transactional
    public void issueTokenForUser(User user) {
        ActivationToken token = buildToken(user);

        activationTokenRepository.save(token);
        log.info("Issued new activation token for user {}", user.getEmail());

        emailService.sendActivationEmail(user.getEmail(), token.getToken());
    }

    /**
     * Finds an activation token by email and token string.
     *
     * @param email user's email
     * @param token token string
     * @return ActivationToken if found
     * @throws ResourceNotFoundException if no matching token is found
     */
    @Transactional(readOnly = true)
    public ActivationToken findByEmailAndToken(String email, String token) {
        log.debug("Looking up activation token for email {}", email);

        return activationTokenRepository.findByUserEmailAndToken(email, token)
                .orElseThrow(() -> {
                    log.warn("Activation token not found for email {}", email);
                    return new ResourceNotFoundException("registration.token.notfound");
                });
    }

    /**
     * Deletes activation tokens for the given email.
     *
     * @param email user's email
     */
    @Transactional
    public void deleteByUserEmail(String email) {
        log.info("Deleting activation token for email {}", email);
        activationTokenRepository.deleteByUserEmail(email);
    }

    /**
     * Validates that the token is not expired.
     *
     * @param token the activation token to validate
     * @throws RegistrationTokenExpiredException if the token is expired
     */
    public void validateToken(ActivationToken token) {
        if (token.getExpiry().isBefore(LocalDateTime.now())) {
            log.warn("Activation token expired for user {}", token.getUser().getEmail());

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