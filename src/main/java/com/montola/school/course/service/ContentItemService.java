package com.montola.school.course.service;

import com.montola.school.course.model.ContentItem;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing content items.
 *
 * Provides methods to create, update, fetch, and soft-delete content items.
 *
 * @author avidewan
 * @date 11/29/25
 */
public interface ContentItemService {

    /**
     * Creates a new content item.
     *
     * @param contentItem the content item to be created
     * @return the saved content item entity
     */
    ContentItem create(ContentItem contentItem);

    /**
     * Retrieves all active (non-deleted) content items.
     *
     * @return list of content items
     */
    List<ContentItem> getAll();

    /**
     * Retrieves a content item by its ID if not deleted.
     *
     * @param id the content item ID
     * @return optional content item entity
     */
    Optional<ContentItem> getById(Long id);

    /**
     * Updates an existing content item.
     *
     * @param id the content item ID
     * @param updated the updated details
     * @return the updated content item entity
     */
    ContentItem update(Long id, ContentItem updated);

    /**
     * Soft deletes a content item by marking it as deleted.
     *
     * @param id the content item ID
     */
    void delete(Long id);
}
