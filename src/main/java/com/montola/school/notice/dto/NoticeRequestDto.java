package com.montola.school.notice.dto;

import com.montola.school.notice.enums.NoticeType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create/update payload for a notice.
 *
 * @author avidewan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    private String message;

    private NoticeType type;

    private String link;

    private Boolean active;

    private Integer orderIndex;
}
