package com.montola.school.course.service;

import com.montola.school.course.model.ClassEntity;

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
     * @param classEntity the class to be created
     * @return the saved class entity
     */
    ClassEntity create(ClassEntity classEntity);

    /**
     * Retrieves all active (non-deleted) classes.
     *
     * @return list of classes
     */
    List<ClassEntity> getAll();

    /**
     * Retrieves a class by its ID if not deleted.
     *
     * @param id the class ID
     * @return optional class entity
     */
    Optional<ClassEntity> getById(Long id);

    /**
     * Updates an existing class.
     *
     * @param id the class ID
     * @param updated the updated details
     * @return the updated class entity
     */
    ClassEntity update(Long id, ClassEntity updated);

    /**
     * Soft deletes a class by marking it as deleted.
     *
     * @param id the class ID
     */
    void delete(Long id);
}