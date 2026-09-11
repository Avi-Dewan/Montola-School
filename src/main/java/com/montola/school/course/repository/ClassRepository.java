package com.montola.school.course.repository;

import com.montola.school.course.model.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author avidewan
 * @date 10/2/25
 */
@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {

    long countByIsDeletedFalse();

    /**
     * Classes whose level is browsed per class (JSC). Used by the shop class tier.
     */
    List<ClassEntity> findByLevel_SplitIntoClassesTrueOrderByIdAsc();

    List<ClassEntity> findByLevel_IdAndLevel_SplitIntoClassesTrueOrderByIdAsc(Long levelId);
}