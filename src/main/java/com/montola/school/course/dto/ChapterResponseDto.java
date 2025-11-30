package com.montola.school.course.dto;

import com.montola.school.course.enums.ChapterStatus;
import lombok.Data;

@Data
public class ChapterResponseDto {
    private Long id;
    private String title;
    private String description;
    private ChapterStatus status;
    private int orderIndex;
    private Long subjectId;
}
