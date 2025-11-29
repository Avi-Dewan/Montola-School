package com.montola.school.course.service;

import com.montola.school.course.dto.GooglePdfContentRequestDto;
import com.montola.school.course.dto.GooglePdfContentResponseDto;

import java.util.List;

/**
 * Service for managing Google PDF content.
 *
 * Provides methods to create, update, fetch, and soft-delete Google PDF content.
 *
 * @author avidewan
 * @date 11/29/25
 */
public interface GooglePdfContentService {

    /**
     * Creates a new Google PDF content.
     *
     * @param dto: the Google PDF content to be created
     * @return the saved Google PDF content entity
     */
    GooglePdfContentResponseDto create(GooglePdfContentRequestDto dto);

    /**
     * Retrieves all active (non-deleted) Google PDF content.
     *
     * @return list of Google PDF content
     */
    List<GooglePdfContentResponseDto> getAll();

    /**
     * Retrieves a Google PDF content by its ID if not deleted.
     *
     * @param id the Google PDF content ID
     * @return optional Google PDF content entity
     */
    GooglePdfContentResponseDto getById(Long id);

    /**
     * Updates an existing Google PDF content.
     *
     * @param id the Google PDF content ID
     * @param updated the updated details
     * @return the updated Google PDF content entity
     */
    GooglePdfContentResponseDto update(Long id, GooglePdfContentRequestDto updated);

    /**
     * Soft deletes a Google PDF content by marking it as deleted.
     *
     * @param id the Google PDF content ID
     */
    void delete(Long id);
}
