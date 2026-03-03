package com.montola.school.course.service.impl;

import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.course.dto.QuizQuestionRequestDto;
import com.montola.school.course.dto.QuizRequestDto;
import com.montola.school.course.dto.QuizResponseDto;
import com.montola.school.course.enums.ContentItemType;
import com.montola.school.course.mapper.QuizMapper;
import com.montola.school.course.model.ContentItem;
import com.montola.school.course.model.contents.Quiz;
import com.montola.school.course.model.contents.quiz.QuizQuestion;
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

        // Link relations using the private helper method
        linkQuizRelations(entity);

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
        existing.getContentItem().setOrderIndex(dto.getOrderIndex());
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

            ContentItem contentItem = entity.getContentItem();
            if (contentItem != null) {
                log.debug("Soft deleting associated content item with ID: {}", contentItem.getId());
                contentItem.setDeleted(true);
                contentItemRepository.save(contentItem);
            }
        });
    }

    @Override
    public QuizResponseDto getByContentItemId(Long contentItemId) {
        log.debug("Fetching quiz by content item ID: {}", contentItemId);
        
        Quiz entity = quizRepository.findByContentItem_Id(contentItemId)
                .filter(q -> !q.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("quiz.notfound"));
                
        return quizMapper.toResponseDto(entity);
    }

    @Override
    @Transactional
    public QuizResponseDto updateByContentItemId(Long contentItemId, QuizRequestDto dto) {
        log.info("Updating quiz with content item ID: {}", contentItemId);

        Quiz existing = quizRepository.findByContentItem_Id(contentItemId)
                .filter(q -> !q.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("quiz.notfound"));

        return update(existing.getId(), dto);
    }

    @Override
    @Transactional
    public void deleteByContentItemId(Long contentItemId) {
        log.warn("Soft deleting quiz with content item ID: {}", contentItemId);

        quizRepository.findByContentItem_Id(contentItemId).ifPresent(entity -> {
            entity.setDeleted(true);
            quizRepository.save(entity);

            ContentItem contentItem = entity.getContentItem();
            if (contentItem != null) {
                log.debug("Soft deleting associated content item with ID: {}", contentItem.getId());
                contentItem.setDeleted(true);
                contentItemRepository.save(contentItem);
            }
        });
    }

    @Override
    @Transactional
    public QuizResponseDto updateQuestions(Long id, List<QuizQuestionRequestDto> questions) {
        log.info("Updating questions for quiz with ID: {}", id);

        Quiz existing = quizRepository.findById(id)
                .filter(q -> !q.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("quiz.notfound"));

        // Clear existing questions
        existing.getQuestions().clear();
        quizRepository.flush();

        if (questions != null) {
            List<QuizQuestion> newQuestions = questions.stream()
                    .map(quizMapper::toEntity)
                    .toList();
            
            existing.getQuestions().addAll(newQuestions);

            // Link relations using the private helper method
            linkQuizRelations(existing);
        }

        Quiz saved = quizRepository.save(existing);

        return quizMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public QuizResponseDto updateQuestionsByContentItemId(Long contentItemId, List<com.montola.school.course.dto.QuizQuestionRequestDto> questions) {
        log.info("Updating questions for quiz with content item ID: {}", contentItemId);

        Quiz existing = quizRepository.findByContentItem_Id(contentItemId)
                .filter(q -> !q.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("quiz.notfound"));

        return updateQuestions(existing.getId(), questions);
    }

    /**
     * Establishes the bidirectional relationship between a Quiz and its questions,
     * and between questions and their specific answer types.
     * @param quiz The Quiz entity whose relations need to be linked.
     */
    private void linkQuizRelations(Quiz quiz) {
        if (quiz.getQuestions() != null) {
            quiz.getQuestions().forEach(question -> {
                question.setQuiz(quiz); // Set parent quiz

                if (question.getOptions() != null) {
                    question.getOptions().forEach(option -> option.setQuestion(question));
                }

                if (question.getWrittenAnswer() != null) {
                    question.getWrittenAnswer().setQuestion(question);
                }

                if (question.getFillBlanks() != null) {
                    question.getFillBlanks().forEach(fb -> fb.setQuestion(question));
                }

                if (question.getTableMatchings() != null) {
                    question.getTableMatchings().forEach(tm -> tm.setQuestion(question));
                }
            });
        }
    }
}
