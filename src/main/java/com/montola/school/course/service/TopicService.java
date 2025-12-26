package com.montola.school.course.service;

import com.montola.school.course.dto.TopicRequestDto;
import com.montola.school.course.dto.TopicResponseDto;

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
     * @param dto the topic to be created
     * @return the saved topic entity
     */
    TopicResponseDto create(TopicRequestDto dto);

    /**
     * Retrieves all active (non-deleted) topics.
     *
     * @return list of topics
     */
    List<TopicResponseDto> getAll();

    /**
     * Retrieves a topic by its ID if not deleted.
     *
     * @param id the topic ID
     * @return optional topic entity
     */
    Optional<TopicResponseDto> getById(Long id);

    /**
     * Updates an existing topic.
     *
     * @param id the topic ID
     * @param dto the updated details
     * @return the updated topic entity
     */
    TopicResponseDto update(Long id, TopicRequestDto dto);

    /**
     * Soft deletes a topic by marking it as deleted.
     *
     * @param id the topic ID
     */
    void delete(Long id);
}
