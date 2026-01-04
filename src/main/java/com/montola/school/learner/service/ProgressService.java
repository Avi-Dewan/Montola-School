package com.montola.school.learner.service;

import com.montola.school.learner.dto.ChapterProgressResponseDto;
import com.montola.school.learner.dto.ContentProgressResponseDto;
import com.montola.school.learner.dto.StudentChapterProgressDto;

import java.util.List;
import java.util.Map;

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
     * Retrieves detailed progress for a specific chapter for a user (status for each content).
     *
     * @param userId    the user ID
     * @param chapterId the chapter ID
     * @return a map of contentId to its completion status
     */
    Map<Long, Boolean> getChapterDetailedProgress(Long userId, Long chapterId);

    /**
     * Retrieves progress for a specific chapter for a user.
     *
     * @param userId    the user ID
     * @param chapterId the chapter ID
     * @return chapter progress DTO
     */
    ChapterProgressResponseDto getChapterProgress(Long userId, Long chapterId);

    /**
     * Retrieves progress for all students enrolled in a specific chapter (Admin/Teacher view).
     *
     * @param chapterId the chapter ID
     * @return list of student progress summaries
     */
    List<StudentChapterProgressDto> getChapterStudentsProgress(Long chapterId);

    /**
     * Retrieves progress for all enrolled chapters for a user.
     *
     * @param userId the user ID
     * @return list of chapter progress DTOs
     */
    List<ChapterProgressResponseDto> getAllEnrolledChapterProgress(Long userId);
}
