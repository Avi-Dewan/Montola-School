package com.montola.school.course.dto;

import com.montola.school.course.enums.QuizType;
import lombok.*;

import java.util.List;


/**
 * @author avidewan
 * @date 11/17/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResponseDto {

    private Long id;
    private QuizType quizType;
    private String title;
    private String instruction;
    private Integer timeLimit;
    private Integer totalMarks;
    private Double passPercentage;
    private Long topicId;
    private String topicTitle;
    private int orderIndex;

    private List<QuizQuestionResponseDto> questions;
}
