package com.montola.school.course.dto;

import com.montola.school.course.enums.ChapterStatus;
import lombok.Data;

import java.util.List;

@Data
public class ChapterResponseDto {
    private Long id;
    private String title;
    private String description;
    private ChapterStatus status;
    private int orderIndex;
    private Long subjectId;
    private String videoId;
    private Double price;
    private boolean isFree;
    private List<TeacherDto> teachers;

    @Data
    public static class TeacherDto {
        private Long id;
        private String fullName;
        private String email;
    }
}
