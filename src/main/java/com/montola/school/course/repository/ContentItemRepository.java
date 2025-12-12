package com.montola.school.course.repository;

import com.montola.school.course.model.ContentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author avidewan
 * @date 11/29/25
 */
@Repository
public interface ContentItemRepository extends JpaRepository<ContentItem, Long> {

    List<ContentItem> findByTopicId(Long topicId);

    List<ContentItem> findByTopicIdIn(List<Long> topicIds);
}
