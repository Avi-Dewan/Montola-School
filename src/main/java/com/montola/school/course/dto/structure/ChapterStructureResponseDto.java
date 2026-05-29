package com.montola.school.course.dto.structure;

import com.montola.school.course.enums.ChapterStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author avidewan
 * @date 12/12/25
 */
@Data
@Builder
public class ChapterStructureResponseDto {

    private Long id;
    private String title;
    private ChapterStatus status;
    private int orderIndex;
    private Long subjectId;
    private String subjectName;
    private Long classId;
    private String className;
    private List<TopicStructureResponseDto> topics;
}
