package com.montola.school.course.dto;

import com.montola.school.course.enums.ChapterStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChapterRequestDto {
    @NotNull
    private Long subjectId;
    @NotBlank
    @Size(max = 200)
    private String title;
    private String description;
    private ChapterStatus status;
    private int orderIndex;
}
