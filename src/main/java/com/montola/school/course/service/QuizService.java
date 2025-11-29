package com.montola.school.course.service;

import com.montola.school.course.dto.QuizRequestDto;
import com.montola.school.course.dto.QuizResponseDto;

import java.util.List;

/**
 * Service for managing quizzes.
 *
 * Provides methods to create, update, fetch, and soft-delete quizzes.
 *
 * @author avidewan
 * @date 11/29/25
 */
public interface QuizService {

    /**
     * Creates a new quiz.
     *
     * @param dto: the quiz to be created
     * @return the saved quiz entity
     */
    QuizResponseDto create(QuizRequestDto dto);

    /**
     * Retrieves all active (non-deleted) quizzes.
     *
     * @return list of quizzes
     */
    List<QuizResponseDto> getAll();

    /**
     * Retrieves a quiz by its ID if not deleted.
     *
     * @param id the quiz ID
     * @return optional quiz entity
     */
    QuizResponseDto getById(Long id);

    /**
     * Updates an existing quiz.
     *
     * @param id the quiz ID
     * @param updated the updated details
     * @return the updated quiz entity
     */
    QuizResponseDto update(Long id, QuizRequestDto updated);

    /**
     * Soft deletes a quiz by marking it as deleted.
     *
     * @param id the quiz ID
     */
    void delete(Long id);
}
