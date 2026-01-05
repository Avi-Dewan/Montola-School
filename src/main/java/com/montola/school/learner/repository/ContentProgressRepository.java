package com.montola.school.learner.repository;

import com.montola.school.learner.model.ContentProgress;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
