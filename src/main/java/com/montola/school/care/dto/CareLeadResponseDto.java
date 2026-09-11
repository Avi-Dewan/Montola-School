package com.montola.school.care.dto;

import com.montola.school.care.enums.CareLeadStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author avidewan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareLeadResponseDto {
    private Long id;
    private String name;
    private String phone;
    private String level;
    private String area;
    private String message;
    private CareLeadStatus status;
    private LocalDateTime createdAt;
}
