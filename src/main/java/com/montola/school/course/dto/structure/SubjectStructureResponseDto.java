package com.montola.school.course.dto.structure;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author avidewan
 * @date 12/12/25
 */
@Data
@Builder
public class SubjectStructureResponseDto {

    private Long id;
    private String name;
    private String description;
    private int orderIndex;
    private Long classId;
    private String className;
    private List<ChapterStructureResponseDto> chapters;
}
