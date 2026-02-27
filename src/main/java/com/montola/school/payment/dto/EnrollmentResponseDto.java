package com.montola.school.payment.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EnrollmentResponseDto {
    private Long enrollmentId;
    private Long userId;
    private Long chapterId;
    private String chapterTitle;
    private LocalDateTime enrolledAt;
    private String status;
}
