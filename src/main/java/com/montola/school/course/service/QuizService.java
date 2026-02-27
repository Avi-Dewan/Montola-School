package com.montola.school.course.service;

import com.montola.school.course.dto.QuizQuestionRequestDto;
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

    /**
     * Retrieves a quiz by its associated content item ID.
     *
     * @param contentItemId the content item ID
     * @return the quiz response DTO
     */
    QuizResponseDto getByContentItemId(Long contentItemId);

    /**
     * Updates an existing quiz by its associated content item ID.
     *
     * @param contentItemId the content item ID
     * @param updated the updated details
     * @return the updated quiz entity
     */
    QuizResponseDto updateByContentItemId(Long contentItemId, QuizRequestDto updated);

    /**
     * Soft deletes a quiz by its associated content item ID.
     *
     * @param contentItemId the content item ID
     */
    void deleteByContentItemId(Long contentItemId);

    /**
     * Updates questions for a quiz.
     *
     * @param id the quiz ID
     * @param questions the list of questions
     * @return the updated quiz response DTO
     */
    QuizResponseDto updateQuestions(Long id, List<QuizQuestionRequestDto> questions);

    /**
     * Updates questions for a quiz by its associated content item ID.
     *
     * @param contentItemId the content item ID
     * @param questions the list of questions
     * @return the updated quiz response DTO
     */
    QuizResponseDto updateQuestionsByContentItemId(Long contentItemId, List<QuizQuestionRequestDto> questions);
}
