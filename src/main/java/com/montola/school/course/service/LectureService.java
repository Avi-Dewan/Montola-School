package com.montola.school.course.service;

import com.montola.school.course.dto.LectureRequestDto;
import com.montola.school.course.dto.LectureResponseDto;

import java.util.List;

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
     * @param dto: the lecture to be created
     * @return the saved lecture entity
     */
    LectureResponseDto create(LectureRequestDto dto);

    /**
     * Retrieves all active (non-deleted) lectures.
     *
     * @return list of lectures
     */
    List<LectureResponseDto> getAll();

    /**
     * Retrieves a lecture by its ID if not deleted.
     *
     * @param id the lecture ID
     * @return optional lecture entity
     */
   LectureResponseDto getById(Long id);

    /**
     * Updates an existing lecture.
     *
     * @param id the lecture ID
     * @param updated the updated details
     * @return the updated lecture entity
     */
    LectureResponseDto update(Long id, LectureRequestDto updated);

    /**
     * Soft deletes a lecture by marking it as deleted.
     *
     * @param id the lecture ID
     */
    void delete(Long id);

    /**
     * Retrieves a lecture by its associated content item ID.
     *
     * @param contentItemId the content item ID
     * @return the lecture response DTO
     */
    LectureResponseDto getByContentItemId(Long contentItemId);

    /**
     * Updates an existing lecture by its associated content item ID.
     *
     * @param contentItemId the content item ID
     * @param updated the updated details
     * @return the updated lecture entity
     */
    LectureResponseDto updateByContentItemId(Long contentItemId, LectureRequestDto updated);

    /**
     * Soft deletes a lecture by its associated content item ID.
     *
     * @param contentItemId the content item ID
     */
    void deleteByContentItemId(Long contentItemId);
}
