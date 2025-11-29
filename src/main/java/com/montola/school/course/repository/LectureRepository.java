package com.montola.school.course.repository;

import com.montola.school.course.model.contents.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author avidewan
 * @date 10/3/25
 */
@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {

    List<Lecture> findByContentItem_Topic_Id(Long topicId);
}
