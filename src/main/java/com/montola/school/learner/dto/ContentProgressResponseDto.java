package com.montola.school.learner.dto;

import com.montola.school.course.enums.ContentItemType;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for content progress response.
 *
 * @author avidewan
 * @date 01/03/2026
 */
@Data
@Builder
public class ContentProgressResponseDto {

    private Long userId;
    private String fullName;
    private Long contentItemId;
    private String contentTitle;
    private ContentItemType contentType;
    private boolean isCompleted;
    private Double quizScore;
}
