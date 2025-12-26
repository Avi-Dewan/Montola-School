package com.montola.school.course.service;

import com.montola.school.course.dto.ClassRequestDto;
import com.montola.school.course.dto.ClassResponseDto;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing classes (e.g., Class 9, Class 10).
 *
 * Provides methods to create, update, fetch, and soft-delete classes.
 *
 * @author avidewan
 * @date 10/3/25
 */
public interface ClassService {

    /**
     * Creates a new class.
     *
     * @param dto the class to be created
     * @return the saved class entity
     */
    ClassResponseDto create(ClassRequestDto dto);

    /**
     * Retrieves all active (non-deleted) classes.
     *
     * @return list of classes
     */
    List<ClassResponseDto> getAll();

    /**
     * Retrieves a class by its ID if not deleted.
     *
     * @param id the class ID
     * @return optional class entity
     */
    Optional<ClassResponseDto> getById(Long id);

    /**
     * Updates an existing class.
     *
     * @param id the class ID
     * @param dto the updated details
     * @return the updated class entity
     */
    ClassResponseDto update(Long id, ClassRequestDto dto);

    /**
     * Soft deletes a class by marking it as deleted.
     *
     * @param id the class ID
     */
    void delete(Long id);
}
