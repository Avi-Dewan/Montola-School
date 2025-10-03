package com.montola.school.course.repository;

import com.montola.school.course.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author avidewan
 * @date 10/2/25
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findByClassId(Long classId);
}