package com.montola.school.course.dto;

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
public class GooglePdfContentResponseDto {

    private Long id;
    private Long contentItemId;
    private String type;
    private String title;
    private Integer pageCount;
    private Long topicId;
    private String topicTitle;
    private int orderIndex;
}
