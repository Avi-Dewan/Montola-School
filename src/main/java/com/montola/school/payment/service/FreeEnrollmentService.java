package com.montola.school.payment.service;

import com.montola.school.payment.dto.EnrollmentResponseDto;

/**
 * Service for handling free chapter enrollments.
 */
public interface FreeEnrollmentService {

    /**
     * Enrolls a student in a free chapter.
     *
     * @param userId    the ID of the student
     * @param chapterId the ID of the chapter
     * @return the enrollment details
     */
    EnrollmentResponseDto enrollInFreeChapter(Long userId, Long chapterId);
}
