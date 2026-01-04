package com.montola.school.course.repository;

import com.montola.school.course.model.FeaturedChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Featured Chapters.
 *
 * @author avidewan
 * @date 1/4/26
 */
@Repository
public interface FeaturedChapterRepository extends JpaRepository<FeaturedChapter, Long> {

    List<FeaturedChapter> findAllByOrderByCreatedAtDesc();

    boolean existsByChapterId(Long chapterId);

    Optional<FeaturedChapter> findByChapterId(Long chapterId);

    void deleteByChapterId(Long chapterId);
}
