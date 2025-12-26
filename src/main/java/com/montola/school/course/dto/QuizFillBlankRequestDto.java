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
public class QuizFillBlankRequestDto {

    private int blankPosition;
    private String correctAnswer;
}
