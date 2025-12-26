package com.montola.school.course.dto.structure;

import com.montola.school.course.enums.ContentItemType;
import lombok.Builder;
import lombok.Data;

/**
 * @author avidewan
 * @date 12/12/25
 */
@Data
@Builder
public class ContentItemStructureResponseDto {

    private Long id;
    private String title;
    private ContentItemType type;
    private int orderIndex;
}
