package com.montola.school.auth.model;

import com.montola.school.auth.enums.Role;
import com.montola.school.common.model.Persistent;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * @author avidewan
 * @date 8/27/25
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false)
    @Email
    @NotBlank
    private String email;

    @Column(nullable = false)
    @NotBlank
    private String passwordHash;

    private String phone;

    private Boolean isActivated = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", length = 50)
    @NotEmpty
    private Set<Role> roles;
}