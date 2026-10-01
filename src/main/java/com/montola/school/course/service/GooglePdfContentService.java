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

    /**
     * Retrieves a Google PDF content by its associated content item ID.
     *
     * @param contentItemId the content item ID
     * @return the Google PDF content response DTO
     */
    GooglePdfContentResponseDto getByContentItemId(Long contentItemId);

    /**
     * Reads the raw bytes of a PDF content item from whichever provider holds it,
     * so the caller can watermark them before serving.
     *
     * @param contentItemId the content item ID
     * @return the file bytes
     */
    byte[] getFileBytes(Long contentItemId);

    /**
     * Stores an uploaded file in application object storage and points the content
     * item at it, moving the document off external hosting.
     *
     * @param contentItemId the content item ID
     * @return the updated content
     */
    GooglePdfContentResponseDto uploadFile(Long contentItemId, byte[] content, String filename, String contentType);

    /**
     * Updates an existing Google PDF content by its associated content item ID.
     *
     * @param contentItemId the content item ID
     * @param updated the updated details
     * @return the updated Google PDF content entity
     */
    GooglePdfContentResponseDto updateByContentItemId(Long contentItemId, GooglePdfContentRequestDto updated);

    /**
     * Soft deletes a Google PDF content by its associated content item ID.
     *
     * @param contentItemId the content item ID
     */
    void deleteByContentItemId(Long contentItemId);
}
