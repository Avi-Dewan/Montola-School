package com.montola.school.course.dto;

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
public class ClassSummaryDto {

    private Long id;
    private String name;
}