package com.montola.school.course.dto;

import com.montola.school.course.enums.ChapterStatus;
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
public class ChapterResponseDto {

    private Long id;
    private String title;
    private String description;
    private ChapterStatus status;

    private Long subjectId;
    private String subjectName;

    private Long createdById;
    private String createdByName;

    private List<TopicSummaryDto> topics;
}