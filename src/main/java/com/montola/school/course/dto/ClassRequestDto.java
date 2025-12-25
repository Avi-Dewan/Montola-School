package com.montola.school.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClassRequestDto {

    @NotBlank
    @Size(max = 100)
    private String name;

    private String description;
}
