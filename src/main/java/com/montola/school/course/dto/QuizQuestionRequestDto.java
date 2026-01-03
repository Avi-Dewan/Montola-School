package com.montola.school.course.dto;

import com.montola.school.course.enums.QuestionType;
import lombok.*;

import java.util.List;

/**
 * @author avidewan
 * @date 12/26/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestionRequestDto {

    private String questionText;

    private QuestionType type;

    private int orderIndex;

    private int marks;

    private List<QuizOptionRequestDto> options;
    private QuizWrittenAnswerRequestDto writtenAnswer;
    private List<QuizFillBlankRequestDto> fillBlanks;
    private List<QuizTableMatchingRequestDto> tableMatchings;
}
