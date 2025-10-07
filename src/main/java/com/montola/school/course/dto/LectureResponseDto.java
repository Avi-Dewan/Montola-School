package com.montola.school.course.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * @author avidewan
 * @date 10/7/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LectureResponseDto {

    private Long id;
    private String title;
    private String videoId;
    private String content;

    private Long topicId;
    private String topicTitle;
}