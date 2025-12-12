package com.montola.school.course.repository;

import com.montola.school.course.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author avidewan
 * @date 10/3/25
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findByChapterId(Long chapterId);

    List<Topic> findByChapterIdIn(List<Long> chapterIds);
}