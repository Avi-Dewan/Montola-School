package com.montola.school.course.service.impl;

import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.course.dto.GooglePdfContentRequestDto;
import com.montola.school.course.dto.GooglePdfContentResponseDto;
import com.montola.school.course.enums.ContentItemType;
import com.montola.school.course.mapper.GooglePdfContentMapper;
import com.montola.school.course.model.ContentItem;
import com.montola.school.course.model.contents.file.GooglePdfContent;
import com.montola.school.course.repository.ContentItemRepository;
import com.montola.school.course.repository.GooglePdfContentRepository;
import com.montola.school.course.repository.TopicRepository;
import com.montola.school.course.service.GooglePdfContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link GooglePdfContentService}.
 *
 * Handles persistence and business logic for Google PDF content.
 *
 * @author avidewan
 * @date 11/17/25
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GooglePdfContentServiceImpl implements GooglePdfContentService {

    private final GooglePdfContentRepository googlePdfContentRepository;
    private final TopicRepository topicRepository;
    private final ContentItemRepository contentItemRepository;

    private final GooglePdfContentMapper googlePdfContentMapper;

    @Override
    @Transactional
    public GooglePdfContentResponseDto create(GooglePdfContentRequestDto dto) {
        log.info("Creating new Google PDF content: {}", dto.getTitle());

        ContentItem contentItem = new ContentItem();
        contentItem.setTopic(
                topicRepository.findById(dto.getTopicId())
                        .orElseThrow(() -> new ResourceNotFoundException("topic.notfound"))
        );
        contentItem.setTitle(dto.getTitle());
        contentItem.setType(ContentItemType.PDF);
        contentItem.setOrderIndex(dto.getOrderIndex());

        ContentItem savedContentItem = contentItemRepository.save(contentItem);

        GooglePdfContent entity = googlePdfContentMapper.toEntity(dto);
        entity.setContentItem(savedContentItem);

        GooglePdfContent saved = googlePdfContentRepository.save(entity);

        return googlePdfContentMapper.toResponseDto(saved);
    }

    @Override
    public List<GooglePdfContentResponseDto> getAll() {
        log.debug("Fetching all active Google PDF content");

        return googlePdfContentRepository.findAll()
                .stream()
                .filter(g -> !g.isDeleted())
                .map(googlePdfContentMapper::toResponseDto)
                .toList();
    }

    @Override
    public GooglePdfContentResponseDto getById(Long id) {
        log.debug("Fetching Google PDF content by ID: {}", id);

        GooglePdfContent entity = googlePdfContentRepository.findById(id)
                .filter(g -> !g.isDeleted())
                .orElseThrow(() -> {
                    log.error("Google PDF content not found with ID: {}", id);

                    return new ResourceNotFoundException("google.pdf.content.notfound");
                });

        return googlePdfContentMapper.toResponseDto(entity);
    }

    @Override
    @Transactional
    public GooglePdfContentResponseDto update(Long id, GooglePdfContentRequestDto dto) {
        log.info("Updating Google PDF content with ID: {}", id);

        GooglePdfContent existing = googlePdfContentRepository.findById(id)
                .filter(g -> !g.isDeleted())
                .orElseThrow(() -> {
                    log.error("Google PDF content not found with ID: {}", id);

                    return new ResourceNotFoundException("google.pdf.content.notfound");
                });

        existing.getContentItem().setTitle(dto.getTitle());
        existing.setGoogleFileId(dto.getGoogleFileId());
        existing.setPageCount(dto.getPageCount());

        GooglePdfContent saved = googlePdfContentRepository.save(existing);

        return googlePdfContentMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.warn("Soft deleting Google PDF content with ID: {}", id);

        googlePdfContentRepository.findById(id).ifPresent(entity -> {
            entity.setDeleted(true);
            googlePdfContentRepository.save(entity);
        });
    }
}
