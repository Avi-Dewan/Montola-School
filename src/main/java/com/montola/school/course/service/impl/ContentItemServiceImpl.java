package com.montola.school.course.service.impl;

import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.course.model.ContentItem;
import com.montola.school.course.repository.ContentItemRepository;
import com.montola.school.course.service.ContentItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link ContentItemService}.
 *
 * Handles persistence and business logic for content items.
 *
 * @author avidewan
 * @date 11/29/25
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ContentItemServiceImpl implements ContentItemService {

    private final ContentItemRepository contentItemRepository;

    @Override
    @Transactional
    public ContentItem create(ContentItem contentItem) {
        log.info("Creating new content item");

        return contentItemRepository.save(contentItem);
    }

    @Override
    public List<ContentItem> getAll() {
        log.debug("Fetching all active content items");

        return contentItemRepository.findAll()
                .stream()
                .filter(c -> !c.isDeleted())
                .toList();
    }

    @Override
    public Optional<ContentItem> getById(Long id) {
        log.debug("Fetching content item by ID: {}", id);

        return contentItemRepository.findById(id)
                .filter(c -> !c.isDeleted());
    }

    @Override
    @Transactional
    public ContentItem update(Long id, ContentItem updated) {
        log.info("Updating content item with ID: {}", id);

        return contentItemRepository.findById(id)
                .map(existing -> {
                    existing.setOrderIndex(updated.getOrderIndex());
                    existing.setType(updated.getType());

                    return contentItemRepository.save(existing);
                })
                .orElseThrow(() -> {
                    log.error("Content item not found with ID: {}", id);

                    return new ResourceNotFoundException("content.item.notfound");
                });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.warn("Soft deleting content item with ID: {}", id);

        contentItemRepository.findById(id).ifPresent(entity -> {
            entity.setDeleted(true);
            contentItemRepository.save(entity);
        });
    }
}
