package com.montola.school.course.dto;

import lombok.*;

import java.util.List;

/**
 * @author avidewan
 * @date 10/7/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopicResponseDto {

    private Long id;
    private String title;
    private String description;

    private Long chapterId;
    private String chapterTitle;

    private List<LectureSummaryDto> lectures;
}
