package com.montola.school.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * @author avidewan
 * @date 10/6/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LectureRequestDto {

    @NotNull
    private Long topicId;

    @NotBlank
    @Size(max = 200)
    private String title;

    /**
     * A YouTube id, or an object-storage key when the video is uploaded here.
     */
    @Size(max = 500)
    private String videoId;

    private String content;

    @NotNull
    private Integer orderIndex;
}
