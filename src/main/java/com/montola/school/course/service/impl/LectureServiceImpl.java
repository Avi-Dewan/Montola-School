package com.montola.school.course.service.impl;

import com.montola.school.common.storage.FileStorageService;
import com.montola.school.common.storage.StorageProperties;
import com.montola.school.course.dto.LectureRequestDto;
import com.montola.school.course.dto.LectureResponseDto;
import com.montola.school.course.enums.ContentItemType;
import com.montola.school.course.enums.StorageProvider;
import com.montola.school.course.mapper.LectureMapper;
import com.montola.school.course.model.ContentItem;
import com.montola.school.course.model.contents.Lecture;
import com.montola.school.course.repository.ContentItemRepository;
import com.montola.school.course.repository.LectureRepository;
import com.montola.school.course.repository.TopicRepository;
import com.montola.school.course.service.LectureService;
import com.montola.school.common.exception.ResourceNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

/**
 * Implementation of {@link LectureService}.
 *
 * Handles persistence and business logic for lectures.
 *
 * @author avidewan
 * @date 10/3/25
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class LectureServiceImpl implements LectureService {

    private final LectureRepository lectureRepository;
    private final TopicRepository topicRepository;
    private final ContentItemRepository contentItemRepository;
    private final FileStorageService fileStorageService;
    private final StorageProperties storageProperties;

    private final LectureMapper lectureMapper;

    @Override
    @Transactional
    public LectureResponseDto create(LectureRequestDto dto) {
        log.info("Creating new lecture: {}", dto.getTitle());

        ContentItem contentItem = new ContentItem();
        contentItem.setTopic(
                topicRepository.findById(dto.getTopicId())
                        .orElseThrow(() -> new ResourceNotFoundException("topic.notfound"))
        );
        contentItem.setTitle(dto.getTitle());
        contentItem.setType(ContentItemType.LECTURE);
        contentItem.setOrderIndex(dto.getOrderIndex());

        ContentItem savedContentItem = contentItemRepository.save(contentItem);

        Lecture entity = lectureMapper.toEntity(dto);
        entity.setContentItem(savedContentItem);

        Lecture saved = lectureRepository.save(entity);

        return toDto(saved);
    }

    @Override
    public List<LectureResponseDto> getAll() {
        log.debug("Fetching all active lectures");

        return lectureRepository.findAll()
                .stream()
                .filter(l -> !l.isDeleted())
                .map(this::toDto)
                .toList();
    }

    @Override
    public LectureResponseDto getById(Long id) {
        log.debug("Fetching lecture by ID: {}", id);

        Lecture entity = lectureRepository.findById(id)
                .filter(l -> !l.isDeleted())
                .orElseThrow(() -> {
                    log.error("Lecture not found with ID: {}", id);

                    return new ResourceNotFoundException("lecture.notfound");
                });

        return toDto(entity);
    }

    @Override
    @Transactional
    public LectureResponseDto update(Long id, LectureRequestDto dto) {
        log.info("Updating lecture with ID: {}", id);

        Lecture existing = lectureRepository.findById(id)
                .filter(l -> !l.isDeleted())
                .orElseThrow(() -> {
                    log.error("Lecture not found with ID: {}", id);

                    return new ResourceNotFoundException("lecture.notfound");
                });
        
        existing.getContentItem().setTitle(dto.getTitle());
        existing.getContentItem().setOrderIndex(dto.getOrderIndex());

        // A blank video id on edit keeps the current video and its provider, because
        // the storage key is never sent back to the client. Supplying one means the
        // lecture points at YouTube again.
        if (dto.getVideoId() != null && !dto.getVideoId().isBlank()) {
            existing.setVideoId(dto.getVideoId());
            existing.setStorageProvider(StorageProvider.GOOGLE_DRIVE);
        }

        existing.setContent(dto.getContent());

        Lecture saved = lectureRepository.save(existing);

        return toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.warn("Soft deleting lecture with ID: {}", id);

        lectureRepository.findById(id).ifPresent(entity -> {
            entity.setDeleted(true);
            lectureRepository.save(entity);

            ContentItem contentItem = entity.getContentItem();
            if (contentItem != null) {
                log.debug("Soft deleting associated content item with ID: {}", contentItem.getId());
                contentItem.setDeleted(true);
                contentItemRepository.save(contentItem);
            }
        });
    }

    @Override
    public LectureResponseDto getByContentItemId(Long contentItemId) {
        log.debug("Fetching lecture by content item ID: {}", contentItemId);
        
        Lecture entity = lectureRepository.findByContentItem_Id(contentItemId)
                .filter(l -> !l.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("lecture.notfound"));

        return toDto(entity);
    }

    @Override
    @Transactional
    public LectureResponseDto uploadVideo(Long contentItemId, byte[] content, String filename, String contentType) {
        log.info("Uploading {} bytes of video for lecture content item {}", content.length, contentItemId);

        Lecture entity = lectureRepository.findByContentItem_Id(contentItemId)
                .filter(l -> !l.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("lecture.notfound"));

        String key = fileStorageService.store(content, filename, contentType);
        entity.setVideoId(key);
        entity.setStorageProvider(StorageProvider.AWS_S3);

        return toDto(lectureRepository.save(entity));
    }

    /**
     * Adds a signed playback URL for lectures held in object storage, and keeps the
     * storage key itself out of the response.
     */
    private LectureResponseDto toDto(Lecture entity) {
        LectureResponseDto dto = lectureMapper.toResponseDto(entity);

        if (entity.getStorageProvider() == StorageProvider.AWS_S3 && entity.getVideoId() != null) {
            dto.setVideoUrl(fileStorageService.url(
                    entity.getVideoId(),
                    Duration.ofSeconds(storageProperties.getS3().getVideoTtlSeconds())));
            dto.setVideoId(null);
        }

        return dto;
    }

    @Override
    @Transactional
    public LectureResponseDto updateByContentItemId(Long contentItemId, LectureRequestDto dto) {
        log.info("Updating lecture with content item ID: {}", contentItemId);

        Lecture existing = lectureRepository.findByContentItem_Id(contentItemId)
                .filter(l -> !l.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("lecture.notfound"));

        return update(existing.getId(), dto);
    }

    @Override
    @Transactional
    public void deleteByContentItemId(Long contentItemId) {
        log.warn("Soft deleting lecture with content item ID: {}", contentItemId);

        lectureRepository.findByContentItem_Id(contentItemId).ifPresent(entity -> {
            entity.setDeleted(true);
            lectureRepository.save(entity);

            ContentItem contentItem = entity.getContentItem();
            if (contentItem != null) {
                log.debug("Soft deleting associated content item with ID: {}", contentItem.getId());
                contentItem.setDeleted(true);
                contentItemRepository.save(contentItem);
            }
        });
    }
}
