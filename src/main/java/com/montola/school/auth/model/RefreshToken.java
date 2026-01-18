package com.montola.school.auth.model;

import com.montola.school.common.model.Persistent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Entity for storing refresh tokens.
 *
 * @author avidewan
 * @date 12/15/2025
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@SequenceGenerator(name = "refresh_token_seq", sequenceName = "refresh_token_seq", allocationSize = 1)
public class RefreshToken extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refresh_token_seq")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;
}
