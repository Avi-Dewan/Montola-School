package com.montola.school.care.dto;

import com.montola.school.care.enums.CareLeadStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin update of a lead's follow-up status.
 *
 * @author avidewan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareLeadStatusUpdateDto {

    @NotNull(message = "Status is required")
    private CareLeadStatus status;
}
