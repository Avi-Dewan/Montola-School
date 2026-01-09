package com.montola.school.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for chapter statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterStatisticsDto {

    private Long chapterId;
    private Long totalEnrolledStudents;
}
