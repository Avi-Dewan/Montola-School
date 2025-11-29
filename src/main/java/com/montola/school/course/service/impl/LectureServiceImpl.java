package com.montola.school.course.service.impl;

import com.montola.school.course.dto.LectureRequestDto;
import com.montola.school.course.dto.LectureResponseDto;
import com.montola.school.course.enums.ContentItemType;
import com.montola.school.course.mapper.LectureMapper;
import com.montola.school.course.model.ContentItem;
import com.montola.school.course.model.contents.Lecture;
import com.montola.school.course.repository.ContentItemRepository;
import com.montola.school.course.repository.LectureRepository;
import com.montola.school.course.repository.TopicRepository;
import com.montola.school.course.service.LectureService;
import com.montola.school.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return lectureMapper.toResponseDto(saved);
    }

    @Override
    public List<LectureResponseDto> getAll() {
        log.debug("Fetching all active lectures");

        return lectureRepository.findAll()
                .stream()
                .filter(l -> !l.isDeleted())
                .map(lectureMapper::toResponseDto)
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

        return lectureMapper.toResponseDto(entity);
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
        existing.setVideoId(dto.getVideoId());
        existing.setContent(dto.getContent());

        Lecture saved = lectureRepository.save(existing);

        return lectureMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.warn("Soft deleting lecture with ID: {}", id);

        lectureRepository.findById(id).ifPresent(entity -> {
            entity.setDeleted(true);
            lectureRepository.save(entity);
        });
    }
}
