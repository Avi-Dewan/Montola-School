package com.montola.school.course.service;

import com.montola.school.course.dto.GooglePdfContentResponseDto;
import com.montola.school.course.dto.LectureResponseDto;
import com.montola.school.course.dto.QuizResponseDto;

/**
 * Service for accessing content items with enrollment-based authorization.
 *
 * @author avidewan
 * @date 12/15/2025
 */
public interface ContentAccessService {

    /**
     * Get content by ID with enrollment check.
     * Returns full DTO based on content type (Lecture/Quiz/PDF).
     *
     * @param contentItemId the content item ID
     * @param userId the user ID requesting access
     * @return content DTO (type depends on content type)
     */
    Object getContentById(Long contentItemId, Long userId);

    /**
     * Get lecture content by content item ID.
     */
    LectureResponseDto getLectureByContentItemId(Long contentItemId, Long userId);

    /**
     * Get quiz content by content item ID.
     */
    QuizResponseDto getQuizByContentItemId(Long contentItemId, Long userId);

    /**
     * Get PDF content by content item ID.
     */
    GooglePdfContentResponseDto getPdfByContentItemId(Long contentItemId, Long userId);
}
