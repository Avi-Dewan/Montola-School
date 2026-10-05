package com.montola.school.notice.dto;

import com.montola.school.notice.enums.NoticeType;
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
public class NoticeResponseDto {
    private Long id;
    private String title;
    private String message;
    private NoticeType type;
    private String link;
    private boolean active;
    private Integer orderIndex;
    private LocalDateTime createdAt;
}
