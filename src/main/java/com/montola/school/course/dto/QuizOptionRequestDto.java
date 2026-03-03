package com.montola.school.course.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class QuizOptionRequestDto {

    private String optionText;

    @JsonProperty("isCorrect")
    private boolean correct;
}
