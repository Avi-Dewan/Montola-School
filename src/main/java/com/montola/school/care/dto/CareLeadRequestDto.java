package com.montola.school.care.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Public Academic Care enquiry form.
 *
 * @author avidewan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareLeadRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(01)[3-9][0-9]{8}$", message = "Enter a valid mobile number (e.g. 01712345678)")
    private String phone;

    private String level;

    private String area;

    private String message;
}
