package com.montola.school.course.dto;

import lombok.Data;

@Data
public class TopicResponseDto {
    private Long id;
    private String title;
    private String description;
    private int orderIndex;
    private Long chapterId;
}
