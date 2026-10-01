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
    private Long contentItemId;
    private String type;
    private String title;

    /**
     * The raw id only for YouTube lectures; object-storage keys stay server-side.
     */
    private String videoId;

    /**
     * Short-lived signed playback URL, set for lectures held in object storage.
     */
    private String videoUrl;

    private String content;

    private Long topicId;
    private String topicTitle;
    private int orderIndex;
}
