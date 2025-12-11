package com.montola.school.course.service.impl;

import com.montola.school.course.dto.TopicRequestDto;
import com.montola.school.course.dto.TopicResponseDto;
import com.montola.school.course.mapper.TopicMapper;
import com.montola.school.course.model.Topic;
import com.montola.school.course.repository.ChapterRepository;
import com.montola.school.course.repository.TopicRepository;
import com.montola.school.course.repository.ContentItemRepository;
import com.montola.school.course.service.TopicService;
import com.montola.school.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of {@link TopicService}.
 *
 * Handles persistence and business logic for topics.
 *
 * @author avidewan
 * @date 10/3/25
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final ChapterRepository chapterRepository;
    private final ContentItemRepository contentItemRepository;
    private final TopicMapper topicMapper;
    private final com.montola.school.course.service.ContentItemService contentItemService;

    @Override
    @Transactional
    public TopicResponseDto create(TopicRequestDto dto) {
        log.info("Creating new topic: {}", dto.getTitle());
        Topic topic = topicMapper.toEntity(dto);
        topic.setChapter(chapterRepository.findById(dto.getChapterId())
                .orElseThrow(() -> new ResourceNotFoundException("chapter.notfound")));
        return topicMapper.toResponseDto(topicRepository.save(topic));
    }

    @Override
    public List<TopicResponseDto> getAll() {
        log.debug("Fetching all active topics");
        return topicRepository.findAll()
                .stream()
                .filter(t -> !t.isDeleted())
                .map(topicMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TopicResponseDto> getById(Long id) {
        log.debug("Fetching topic by ID: {}", id);
        return topicRepository.findById(id)
                .filter(t -> !t.isDeleted())
                .map(topicMapper::toResponseDto);
    }

    @Override
    @Transactional
    public TopicResponseDto update(Long id, TopicRequestDto dto) {
        log.info("Updating topic with ID: {}", id);
        return topicRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(dto.getTitle());
                    existing.setDescription(dto.getDescription());
                    existing.setOrderIndex(dto.getOrderIndex());
                    return topicMapper.toResponseDto(topicRepository.save(existing));
                })
                .orElseThrow(() -> {
                    log.error("Topic not found with ID: {}", id);
                    return new ResourceNotFoundException("topic.notfound");
                });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.warn("Soft deleting topic with ID: {}", id);
        topicRepository.findById(id).ifPresent(entity -> {
            // Cascade delete content items
            contentItemRepository.findByTopicId(id).forEach(contentItem -> {
                contentItemService.delete(contentItem.getId());
            });

            entity.setDeleted(true);
            topicRepository.save(entity);
        });
    }
}
