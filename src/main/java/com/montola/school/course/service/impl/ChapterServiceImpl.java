package com.montola.school.course.service.impl;

import com.montola.school.course.dto.ChapterRequestDto;
import com.montola.school.course.dto.ChapterResponseDto;
import com.montola.school.course.mapper.ChapterMapper;
import com.montola.school.course.model.Chapter;
import com.montola.school.course.repository.ChapterRepository;
import com.montola.school.course.repository.SubjectRepository;
import com.montola.school.course.service.ChapterService;
import com.montola.school.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ChapterService}.
 * Handles persistence and business logic for chapters.
 *
 * @author avidewan
 * @date 10/3/25
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;
    private final SubjectRepository subjectRepository;
    private final com.montola.school.course.repository.TopicRepository topicRepository;
    private final ChapterMapper chapterMapper;
    private final com.montola.school.course.service.TopicService topicService;

    @Override
    @Transactional
    public ChapterResponseDto create(ChapterRequestDto dto) {
        log.info("Creating new chapter: {}", dto.getTitle());
        Chapter chapter = chapterMapper.toEntity(dto);
        chapter.setSubject(subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("subject.notfound")));
        return chapterMapper.toResponseDto(chapterRepository.save(chapter));
    }

    @Override
    public List<ChapterResponseDto> getAll() {
        log.debug("Fetching all active chapters");
        return chapterRepository.findAll()
                .stream()
                .filter(c -> !c.isDeleted())
                .map(chapterMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ChapterResponseDto> getById(Long id) {
        log.debug("Fetching chapter by ID: {}", id);
        return chapterRepository.findById(id)
                .filter(c -> !c.isDeleted())
                .map(chapterMapper::toResponseDto);
    }

    @Override
    @Transactional
    public ChapterResponseDto update(Long id, ChapterRequestDto dto) {
        log.info("Updating chapter with ID: {}", id);
        return chapterRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(dto.getTitle());
                    existing.setDescription(dto.getDescription());
                    existing.setStatus(dto.getStatus());
                    existing.setOrderIndex(dto.getOrderIndex());
                    return chapterMapper.toResponseDto(chapterRepository.save(existing));
                })
                .orElseThrow(() -> {
                    log.error("Chapter not found with ID: {}", id);
                    return new ResourceNotFoundException("chapter.notfound");
                });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.warn("Soft deleting chapter with ID: {}", id);
        chapterRepository.findById(id).ifPresent(entity -> {
            // Cascade delete topics
            topicRepository.findByChapterId(id).forEach(topic -> {
                topicService.delete(topic.getId());
            });

            entity.setDeleted(true);
            chapterRepository.save(entity);
        });
    }
}
