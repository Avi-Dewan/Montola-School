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
    private String videoId;
    private Double price;
    private boolean isFree;
    private AuthorDto author;

    @Data
    public static class AuthorDto {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
    }
}
