package com.montola.school.learner.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for administrative overview of a student's progress in a chapter.
 *
 * @author avidewan
 * @date 01/03/2026
 */
@Data
@Builder
public class StudentChapterProgressDto {

    private Long studentId;
    private String studentName;
    private Double progressPercentage;
    private boolean isCompleted;
}
