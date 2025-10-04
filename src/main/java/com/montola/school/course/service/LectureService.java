package com.montola.school.course.service;

import com.montola.school.course.model.Lecture;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing lectures.
 *
 * Provides methods to create, update, fetch, and soft-delete lectures.
 *
 * @author avidewan
 * @date 10/3/25
 */
public interface LectureService {

    /**
     * Creates a new lecture.
     *
     * @param lecture the lecture to be created
     * @return the saved lecture entity
     */
    Lecture create(Lecture lecture);

    /**
     * Retrieves all active (non-deleted) lectures.
     *
     * @return list of lectures
     */
    List<Lecture> getAll();

    /**
     * Retrieves a lecture by its ID if not deleted.
     *
     * @param id the lecture ID
     * @return optional lecture entity
     */
    Optional<Lecture> getById(Long id);

    /**
     * Updates an existing lecture.
     *
     * @param id the lecture ID
     * @param updated the updated details
     * @return the updated lecture entity
     */
    Lecture update(Long id, Lecture updated);

    /**
     * Soft deletes a lecture by marking it as deleted.
     *
     * @param id the lecture ID
     */
    void delete(Long id);
}
