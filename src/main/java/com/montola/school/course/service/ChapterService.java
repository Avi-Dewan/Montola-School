package com.montola.school.course.service;

import com.montola.school.course.model.Chapter;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing chapters within subjects.
 *
 * Provides methods to create, update, fetch, and soft-delete chapters.
 *
 * @author avidewan
 * @date 10/3/25
 */
public interface ChapterService {

    /**
     * Creates a new chapter.
     *
     * @param chapter the chapter entity to be created
     * @return the saved chapter entity
     */
    Chapter create(Chapter chapter);

    /**
     * Retrieves all active (non-deleted) chapters.
     *
     * @return list of chapters
     */
    List<Chapter> getAll();

    /**
     * Retrieves a chapter by its ID if not deleted.
     *
     * @param id the chapter ID
     * @return optional chapter entity
     */
    Optional<Chapter> getById(Long id);

    /**
     * Updates an existing chapter.
     *
     * @param id the chapter ID
     * @param updated the updated chapter details
     * @return the updated chapter entity
     */
    Chapter update(Long id, Chapter updated);

    /**
     * Soft deletes a chapter by marking it as deleted.
     *
     * @param id the chapter ID
     */
    void delete(Long id);
}

