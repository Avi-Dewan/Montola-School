package com.montola.school.course.repository;

import com.montola.school.course.model.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author avidewan
 * @date 10/2/25
 */
@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {

    long countByIsDeletedFalse();
}