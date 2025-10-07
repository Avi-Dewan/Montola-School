package com.montola.school.course.dto;

import lombok.*;

import java.util.List;

/**
 * @author avidewan
 * @date 10/7/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassResponseDto {

    private Long id;
    private String name;
    private String description;

    private List<SubjectSummaryDto> subjects;
}
