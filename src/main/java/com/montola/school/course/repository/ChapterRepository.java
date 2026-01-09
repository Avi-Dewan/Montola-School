package com.montola.school.course.repository;

import com.montola.school.course.enums.ChapterStatus;
import com.montola.school.course.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author avidewan
 * @date 10/2/25
 */
@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    List<Chapter> findBySubjectId(Long subjectId);

    List<Chapter> findBySubjectIdIn(List<Long> subjectIds);

    List<Chapter> findAllByStatus(ChapterStatus status);

    List<Chapter> findAllByStatusAndIsFreeTrue(ChapterStatus status);
    
    long countByStatus(ChapterStatus status); // Keeping for backward compat if needed, or remove? I'll keep but add safe one.

    long countByStatusAndIsDeletedFalse(ChapterStatus status);

    long countByIsFreeAndIsDeletedFalse(boolean isFree);

    long countByIsDeletedFalse();
}