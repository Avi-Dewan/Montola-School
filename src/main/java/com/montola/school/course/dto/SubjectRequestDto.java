package com.montola.school.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubjectRequestDto {
    @NotNull
    private Long classId;
    @NotBlank
    @Size(max = 100)
    private String name;
    private String description;
    private int orderIndex;
}
