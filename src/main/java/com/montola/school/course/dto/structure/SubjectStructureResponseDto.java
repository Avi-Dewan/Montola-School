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
    private int orderIndex;
    private List<ChapterStructureResponseDto> chapters;
}
