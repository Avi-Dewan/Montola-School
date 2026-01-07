package com.montola.school.learner.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for chapter progress response.
 *
 * @author avidewan
 * @date 01/03/2026
 */
@Data
@Builder
public class ChapterProgressResponseDto {

    private Long chapterId;
    private String chapterTitle;
    private Double progressPercentage;
    private boolean isCompleted;
}
