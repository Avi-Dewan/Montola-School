package com.montola.school.learner.repository;

import com.montola.school.learner.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Enrollment entities.
 *
 * @author avidewan
 * @date 12/14/2025
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByUserIdAndChapterId(Long userId, Long chapterId);

    Optional<Enrollment> findByUserIdAndChapterId(Long userId, Long chapterId);

    List<Enrollment> findByUserId(Long userId);
}
