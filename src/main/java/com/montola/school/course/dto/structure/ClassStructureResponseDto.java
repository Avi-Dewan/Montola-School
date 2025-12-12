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
public class ClassStructureResponseDto {

    private Long id;
    private String name;
    private String description;
    private List<SubjectStructureResponseDto> subjects;
}
