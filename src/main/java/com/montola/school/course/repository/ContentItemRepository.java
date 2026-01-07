package com.montola.school.course.repository;

import com.montola.school.course.model.ContentItem;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    long countByTopic_Chapter_Id(Long chapterId);

    @Query("SELECT c FROM ContentItem c WHERE c.topic.chapter.id = :chapterId " +
            "AND (c.topic.orderIndex < :currentTopicOrder OR (c.topic.id = :currentTopicId AND c.orderIndex < :currentContentOrder)) " +
            "ORDER BY c.topic.orderIndex DESC, c.orderIndex DESC")
    List<ContentItem> findPreviousContentItems(@Param("chapterId") Long chapterId,
                                               @Param("currentTopicOrder") int currentTopicOrder,
                                               @Param("currentTopicId") Long currentTopicId,
                                               @Param("currentContentOrder") int currentContentOrder,
                                               Pageable pageable);
}
