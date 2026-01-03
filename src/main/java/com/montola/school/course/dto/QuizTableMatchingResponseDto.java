package com.montola.school.course.dto;

import lombok.*;

/**
 * @author avidewan
 * @date 12/26/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizTableMatchingResponseDto {

    private Long id;
    private String leftItem;
    private String rightItem;
    private int orderIndex;
}
