package com.montola.school.auth.repository;

import com.montola.school.auth.model.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author avidewan
 * @date 9/1/25
 */
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Long> {

    Optional<ActivationToken> findByUserEmailAndToken(String email, String token);

    void deleteByUserEmail(String email);
}