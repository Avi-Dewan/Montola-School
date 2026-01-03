package com.montola.school.learner.service;

import com.montola.school.learner.dto.ChapterProgressResponseDto;
import com.montola.school.learner.dto.ContentProgressResponseDto;
import java.util.List;

/**
 * Service for tracking student progress.
 *
 * @author avidewan
 * @date 12/13/2025
 */
public interface ProgressService {

    /**
     * Marks a content item as completed by a user.
     *
     * @param userId        ID of the user
     * @param contentItemId ID of the content item
     * @return The updated ContentProgress DTO
     */
    ContentProgressResponseDto markComplete(Long userId, Long contentItemId);

    /**
     * Submits a quiz score for a content item.
     *
     * @param userId        ID of the user
     * @param contentItemId ID of the content item
     * @param score         The score achieved
     * @return The updated ContentProgress DTO
     */
    ContentProgressResponseDto submitQuizResult(Long userId, Long contentItemId, Double score);

    /**
     * Retrieves all progress records for a specific user.
     *
     * @param userId the user ID
     * @return list of progress records
     */
    List<ContentProgressResponseDto> getStudentProgress(Long userId);

    /**
     * Retrieves progress for a specific chapter for a user.
     *
     * @param userId    the user ID
     * @param chapterId the chapter ID
     * @return chapter progress DTO
     */
    ChapterProgressResponseDto getChapterProgress(Long userId, Long chapterId);

    /**
     * Retrieves progress for all enrolled chapters for a user.
     *
     * @param userId the user ID
     * @return list of chapter progress DTOs
     */
    List<ChapterProgressResponseDto> getAllEnrolledChapterProgress(Long userId);
}
