package com.montola.school.course.dto;

import com.montola.school.course.enums.ChapterStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ChapterRequestDto {

    @NotNull
    private Long subjectId;

    @NotBlank
    @Size(max = 200)
    private String title;

    private String description;

    private ChapterStatus status;
}