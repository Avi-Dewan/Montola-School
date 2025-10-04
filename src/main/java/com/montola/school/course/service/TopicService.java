package com.montola.school.course.service;

import com.montola.school.course.model.Topic;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing topics.
 *
 * Provides methods to create, update, fetch, and soft-delete topics.
 *
 * @author avidewan
 * @date 10/3/25
 */
public interface TopicService {

    /**
     * Creates a new topic.
     *
     * @param topic the topic to be created
     * @return the saved topic entity
     */
    Topic create(Topic topic);

    /**
     * Retrieves all active (non-deleted) topics.
     *
     * @return list of topics
     */
    List<Topic> getAll();

    /**
     * Retrieves a topic by its ID if not deleted.
     *
     * @param id the topic ID
     * @return optional topic entity
     */
    Optional<Topic> getById(Long id);

    /**
     * Updates an existing topic.
     *
     * @param id the topic ID
     * @param updated the updated details
     * @return the updated topic entity
     */
    Topic update(Long id, Topic updated);

    /**
     * Soft deletes a topic by marking it as deleted.
     *
     * @param id the topic ID
     */
    void delete(Long id);
}