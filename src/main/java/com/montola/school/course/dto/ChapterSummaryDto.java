package com.montola.school.course.dto;

import com.montola.school.course.enums.ChapterStatus;
import lombok.*;

/**
 * @author avidewan
 * @date 10/7/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterSummaryDto {

    private Long id;
    private String title;
    private ChapterStatus status;
}

