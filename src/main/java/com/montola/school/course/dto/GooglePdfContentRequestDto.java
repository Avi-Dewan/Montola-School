package com.montola.school.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * @author avidewan
 * @date 11/17/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GooglePdfContentRequestDto {

    @NotNull
    private Long topicId;

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    @Size(max = 200)
    private String googleFileId;

    private Integer pageCount;

    @NotNull
    private Integer orderIndex;
}
