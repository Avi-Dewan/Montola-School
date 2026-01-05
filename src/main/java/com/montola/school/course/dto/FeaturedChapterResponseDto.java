package com.montola.school.course.dto;

import  lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class FeaturedChapterResponseDto {

    private Long id;
    private Long chapterId;
    private String title;
    private String description;
    private String subjectName;
    private String className;
    private Double price;
    private boolean isFree;
    private LocalDateTime featuredAt;
}
