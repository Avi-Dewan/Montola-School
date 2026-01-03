package com.montola.school.learner.service;

import com.montola.school.learner.model.ContentProgress;
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
     * @return The updated ContentProgress
     */
    ContentProgress markComplete(Long userId, Long contentItemId);

    /**
     * Submits a quiz score for a content item.
     *
     * @param userId        ID of the user
     * @param contentItemId ID of the content item
     * @param score         The score achieved
     * @return The updated ContentProgress
     */
    ContentProgress submitQuizResult(Long userId, Long contentItemId, Double score);

    /**
     * Retrieves all progress records for a specific user.
     *
     * @param userId the user ID
     * @return list of progress records
     */
    List<ContentProgress> getStudentProgress(Long userId);
}
