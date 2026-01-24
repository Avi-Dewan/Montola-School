package com.montola.school.course.dto;

import lombok.Data;

@Data
public class SubjectResponseDto {
    private Long id;
    private String name;
    private String description;
    private int orderIndex;
    private Long classId;
    private String className;
}
