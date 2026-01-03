package com.montola.school.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TopicRequestDto {

    @NotNull
    private Long chapterId;

    @NotBlank
    @Size(max = 200)
    private String title;

    private String description;

    private int orderIndex;
}
