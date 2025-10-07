package com.montola.school.course.dto;

import lombok.*;

import java.util.List;

/**
 * @author avidewan
 * @date 10/7/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectResponseDto {

    private Long id;
    private String name;
    private String description;

    private Long classId;
    private String className;

    private List<ChapterSummaryDto> chapters;
}

