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
public class TopicStructureResponseDto {

    private Long id;
    private String title;
    private int orderIndex;
    private List<ContentItemStructureResponseDto> contentItems;
}
