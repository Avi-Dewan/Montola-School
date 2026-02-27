package com.montola.school.auth.repository;

import com.montola.school.auth.model.RefreshToken;
import com.montola.school.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for RefreshToken entities.
 *
 * @author avidewan
 * @date 12/15/2025
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUser(User user);
}
