package com.montola.school.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for refreshing an access token.
 *
 * @author avidewan
 * @date 12/15/2025
 */
@Data
public class RefreshTokenRequest {

    @NotBlank
    private String refreshToken;
}
