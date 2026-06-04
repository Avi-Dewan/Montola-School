package com.montola.school.learner.repository;

import com.montola.school.learner.model.ContentProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing ContentProgress entities.
 *
 * @author avidewan
 * @date 12/13/2025
 */
@Repository
public interface ContentProgressRepository extends JpaRepository<ContentProgress, Long> {

    Optional<ContentProgress> findByUserIdAndContentItemId(Long userId, Long contentItemId);

    List<ContentProgress> findByUserId(Long userId);

    long countByUserIdAndContentItem_Topic_Chapter_IdAndIsCompletedTrue(Long userId, Long chapterId);

    List<ContentProgress> findByUserIdAndContentItem_Topic_Chapter_Id(Long userId, Long chapterId);

    /**
     * Count content items before the current one (in the same chapter) that are NOT completed by the user.
     * Uses topic.orderIndex → content.orderIndex → content.id as the ordering tiebreaker chain.
     * Excludes soft-deleted content items and topics.
     * Returns 0 if all previous items are completed (access should be granted).
     */
    @Query("SELECT COUNT(c) FROM ContentItem c WHERE c.topic.chapter.id = :chapterId " +
            "AND c.isDeleted = false AND c.topic.isDeleted = false " +
            "AND (" +
            "  c.topic.orderIndex < :currentTopicOrder " +
            "  OR (c.topic.orderIndex = :currentTopicOrder AND c.orderIndex < :currentContentOrder) " +
            "  OR (c.topic.orderIndex = :currentTopicOrder AND c.orderIndex = :currentContentOrder AND c.id < :currentItemId)" +
            ") " +
            "AND NOT EXISTS (" +
            "  SELECT 1 FROM ContentProgress cp " +
            "  WHERE cp.contentItem.id = c.id AND cp.user.id = :userId AND cp.isCompleted = true" +
            ")")
    long countIncompletePreviousItems(@Param("userId") Long userId,
                                      @Param("chapterId") Long chapterId,
                                      @Param("currentTopicOrder") int currentTopicOrder,
                                      @Param("currentContentOrder") int currentContentOrder,
                                      @Param("currentItemId") Long currentItemId);
}
