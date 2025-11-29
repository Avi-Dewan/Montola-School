package com.montola.school.course.service.impl;

import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.course.dto.QuizRequestDto;
import com.montola.school.course.dto.QuizResponseDto;
import com.montola.school.course.enums.ContentItemType;
import com.montola.school.course.mapper.QuizMapper;
import com.montola.school.course.model.ContentItem;
import com.montola.school.course.model.contents.Quiz;
import com.montola.school.course.repository.ContentItemRepository;
import com.montola.school.course.repository.QuizRepository;
import com.montola.school.course.repository.TopicRepository;
import com.montola.school.course.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link QuizService}.
 *
 * Handles persistence and business logic for quizzes.
 *
 * @author avidewan
 * @date 11/17/25
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final TopicRepository topicRepository;
    private final ContentItemRepository contentItemRepository;

    private final QuizMapper quizMapper;

    @Override
    @Transactional
    public QuizResponseDto create(QuizRequestDto dto) {
        log.info("Creating new quiz: {}", dto.getTitle());

        ContentItem contentItem = new ContentItem();
        contentItem.setTopic(
                topicRepository.findById(dto.getTopicId())
                        .orElseThrow(() -> new ResourceNotFoundException("topic.notfound"))
        );
        contentItem.setTitle(dto.getTitle());
        contentItem.setType(ContentItemType.QUIZ);
        contentItem.setOrderIndex(dto.getOrderIndex());

        ContentItem savedContentItem = contentItemRepository.save(contentItem);

        Quiz entity = quizMapper.toEntity(dto);
        entity.setContentItem(savedContentItem);

        Quiz saved = quizRepository.save(entity);

        return quizMapper.toResponseDto(saved);
    }

    @Override
    public List<QuizResponseDto> getAll() {
        log.debug("Fetching all active quizzes");

        return quizRepository.findAll()
                .stream()
                .filter(q -> !q.isDeleted())
                .map(quizMapper::toResponseDto)
                .toList();
    }

    @Override
    public QuizResponseDto getById(Long id) {
        log.debug("Fetching quiz by ID: {}", id);

        Quiz entity = quizRepository.findById(id)
                .filter(q -> !q.isDeleted())
                .orElseThrow(() -> {
                    log.error("Quiz not found with ID: {}", id);

                    return new ResourceNotFoundException("quiz.notfound");
                });

        return quizMapper.toResponseDto(entity);
    }

    @Override
    @Transactional
    public QuizResponseDto update(Long id, QuizRequestDto dto) {
        log.info("Updating quiz with ID: {}", id);

        Quiz existing = quizRepository.findById(id)
                .filter(q -> !q.isDeleted())
                .orElseThrow(() -> {
                    log.error("Quiz not found with ID: {}", id);

                    return new ResourceNotFoundException("quiz.notfound");
                });

        existing.getContentItem().setTitle(dto.getTitle());
        existing.setInstruction(dto.getInstruction());
        existing.setTimeLimit(dto.getTimeLimit());
        existing.setTotalMarks(dto.getTotalMarks());
        existing.setPassPercentage(dto.getPassPercentage());
        existing.setQuizType(dto.getQuizType());

        Quiz saved = quizRepository.save(existing);

        return quizMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.warn("Soft deleting quiz with ID: {}", id);

        quizRepository.findById(id).ifPresent(entity -> {
            entity.setDeleted(true);
            quizRepository.save(entity);
        });
    }
}
