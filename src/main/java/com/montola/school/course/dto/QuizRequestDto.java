package com.montola.school.course.dto;

import com.montola.school.course.enums.QuizType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.math.BigDecimal;

/**
 * @author avidewan
 * @date 11/17/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizRequestDto {

    @NotNull
    private Long topicId;

    @NotNull
    private QuizType quizType;

    @NotBlank
    @Size(max = 200)
    private String title;

    private String instruction;

    private Integer timeLimit;

    private Integer totalMarks;

    private BigDecimal passPercentage;

    @NotNull
    private Integer orderIndex;
}
