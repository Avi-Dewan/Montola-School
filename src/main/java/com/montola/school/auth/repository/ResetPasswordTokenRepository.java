package com.montola.school.auth.repository;

import com.montola.school.auth.model.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author avidewan
 * @date 9/10/25
 */
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {

    Optional<ResetPasswordToken> findByUserEmailAndToken(String email, String token);

    void deleteByUserEmail(String email);
}
