package com.montola.school.course.service;

import com.montola.school.course.dto.SubjectRequestDto;
import com.montola.school.course.dto.SubjectResponseDto;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing subjects (e.g., Math, Science).
 *
 * Provides methods to create, update, fetch, and soft-delete subjects.
 *
 * @author avidewan
 * @date 10/3/25
 */
public interface SubjectService {

    /**
     * Creates a new subject.
     *
     * @param dto the subject to be created
     * @return the saved subject entity
     */
    SubjectResponseDto create(SubjectRequestDto dto);

    /**
     * Retrieves all active (non-deleted) subjects.
     *
     * @return list of subjects
     */
    List<SubjectResponseDto> getAll();

    /**
     * Retrieves a subject by its ID if not deleted.
     *
     * @param id the subject ID
     * @return optional subject entity
     */
    Optional<SubjectResponseDto> getById(Long id);

    /**
     * Updates an existing subject.
     *
     * @param id the subject ID
     * @param dto the updated details
     * @return the updated subject entity
     */
    SubjectResponseDto update(Long id, SubjectRequestDto dto);

    /**
     * Soft deletes a subject by marking it as deleted.
     *
     * @param id the subject ID
     */
    void delete(Long id);
}
