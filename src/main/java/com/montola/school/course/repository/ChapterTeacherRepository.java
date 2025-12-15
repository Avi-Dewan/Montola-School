package com.montola.school.course.repository;

import com.montola.school.course.model.ChapterTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ChapterTeacher entities.
 *
 * @author avidewan
 * @date 12/15/2025
 */
@Repository
public interface ChapterTeacherRepository extends JpaRepository<ChapterTeacher, Long> {

    boolean existsByChapterIdAndTeacherId(Long chapterId, Long teacherId);

    List<ChapterTeacher> findByChapterId(Long chapterId);

    List<ChapterTeacher> findByTeacherId(Long teacherId);

    void deleteByChapterIdAndTeacherId(Long chapterId, Long teacherId);
}
