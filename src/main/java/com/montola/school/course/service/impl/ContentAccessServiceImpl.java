package com.montola.school.course.service.impl;

import com.montola.school.common.exception.AccessDeniedCustomException;
import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.course.dto.GooglePdfContentResponseDto;
import com.montola.school.course.dto.LectureResponseDto;
import com.montola.school.course.dto.QuizResponseDto;
import com.montola.school.course.enums.ContentItemType;
import com.montola.school.course.model.Chapter;
import com.montola.school.course.model.ContentItem;
import com.montola.school.course.repository.ContentItemRepository;
import com.montola.school.course.service.ContentAccessService;
import com.montola.school.course.service.GooglePdfContentService;
import com.montola.school.course.service.LectureService;
import com.montola.school.course.service.QuizService;
import com.montola.school.learner.repository.ContentProgressRepository;
import com.montola.school.learner.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of ContentAccessService.
 *
 * @author avidewan
 * @date 12/15/2025
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ContentAccessServiceImpl implements ContentAccessService {

    private final ContentItemRepository contentItemRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ContentProgressRepository progressRepository;
    private final LectureService lectureService;
    private final QuizService quizService;
    private final GooglePdfContentService pdfService;

    @Override
    public Object getContentById(Long contentItemId, Long userId, boolean isAdminOrManagerOrTeacher) {
        log.info("User {} requesting access to content {}", userId, contentItemId);

        // Get content item
        ContentItem contentItem = contentItemRepository.findById(contentItemId)
                .orElseThrow(() -> new ResourceNotFoundException("course.content.notfound"));

        // Get chapter ID from content item hierarchy
        Chapter chapter = contentItem.getTopic().getChapter();

        checkAccess(userId, isAdminOrManagerOrTeacher, chapter, contentItem);

        // Return full DTO based on content type
        ContentItemType type = contentItem.getType();
        log.info("Loading {} content for user {}", type, userId);

        return switch (type) {
            case LECTURE -> getLectureByContentItemId(contentItemId, userId);
            case QUIZ -> getQuizByContentItemId(contentItemId, userId);
            case PDF -> getPdfByContentItemId(contentItemId, userId);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    @Override
    public LectureResponseDto getLectureByContentItemId(Long contentItemId, Long userId) {
        return lectureService.getByContentItemId(contentItemId);
    }

    @Override
    public QuizResponseDto getQuizByContentItemId(Long contentItemId, Long userId) {
        return quizService.getByContentItemId(contentItemId);
    }

    @Override
    public GooglePdfContentResponseDto getPdfByContentItemId(Long contentItemId, Long userId) {
        return pdfService.getByContentItemId(contentItemId);
    }

    private void checkAccess(Long userId, boolean isAdminOrManagerOrTeacher, Chapter chapter, ContentItem currentItem) {
        if (isAdminOrManagerOrTeacher) return;

        if (!chapter.isFree() && !enrollmentRepository.existsByUserIdAndChapterId(userId, chapter.getId())) {
            log.warn("User {} not enrolled in chapter {} for accessing content", userId, chapter.getId());
            throw new AccessDeniedCustomException("content.purchase.toAccess");
        }

        // Sequential Access Check
        checkPrerequisites(userId, chapter, currentItem);
    }

    private void checkPrerequisites(Long userId, Chapter chapter, ContentItem currentItem) {
        log.debug("Checking prerequisites for user {} on content {}", userId, currentItem.getId());

        long incompleteCount = progressRepository.countIncompletePreviousItems(
                userId,
                chapter.getId(),
                currentItem.getTopic().getOrderIndex(),
                currentItem.getOrderIndex(),
                currentItem.getId()
        );

        if (incompleteCount > 0) {
            log.warn("User {} blocked from content {}. {} previous content item(s) not completed.",
                    userId, currentItem.getId(), incompleteCount);
            throw new AccessDeniedCustomException("content.complete.previous");
        }
    }
}
