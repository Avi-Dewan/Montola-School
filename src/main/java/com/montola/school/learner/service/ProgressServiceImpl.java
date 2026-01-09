package com.montola.school.learner.service;

import com.montola.school.auth.model.User;
import com.montola.school.auth.repository.UserRepository;
import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.course.model.ContentItem;
import com.montola.school.course.repository.ContentItemRepository;
import com.montola.school.learner.dto.ChapterProgressResponseDto;
import com.montola.school.learner.dto.ContentProgressResponseDto;
import com.montola.school.learner.model.ContentProgress;
import com.montola.school.learner.model.Enrollment;
import com.montola.school.learner.repository.ContentProgressRepository;
import com.montola.school.learner.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.montola.school.learner.dto.StudentChapterProgressDto;

/**
 * Implementation of ProgressService.
 *
 * @author avidewan
 * @date 12/13/2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressServiceImpl implements ProgressService {

    private final ContentProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final ContentItemRepository contentItemRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional
    public ContentProgressResponseDto markComplete(Long userId, Long contentItemId) {
        log.info("Marking content {} as complete for user {}", contentItemId, userId);

        ContentProgress progress = getOrCreateProgress(userId, contentItemId); // TODO: be stricter in future, only allow for pdf and lecture
        progress.setCompleted(true);
        progress.setLastAccessed(LocalDateTime.now());

        progress = progressRepository.save(progress);
        
        // Update enrollment progress
        updateEnrollmentProgress(userId, progress.getContentItem().getTopic().getChapter().getId(), progress.getContentItem());

        return mapToContentProgressDto(progress);
    }

    @Override
    @Transactional
    public ContentProgressResponseDto submitQuizResult(Long userId, Long contentItemId, Double score) {
        log.info("Submitting quiz score {} for content {} user {}", score, contentItemId, userId);

        ContentProgress progress = getOrCreateProgress(userId, contentItemId);
        progress.setQuizScore(score);
        progress.setLastAccessed(LocalDateTime.now());
        progress.setCompleted(true);

        progress = progressRepository.save(progress);

        // Update enrollment progress
        updateEnrollmentProgress(userId, progress.getContentItem().getTopic().getChapter().getId(), progress.getContentItem());

        return mapToContentProgressDto(progress);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Boolean> getChapterDetailedProgress(Long userId, Long chapterId) {
        log.debug("Fetching detailed chapter progress for user {} chapter {}", userId, chapterId);
        
        List<ContentProgress> progressList = progressRepository.findByUserIdAndContentItem_Topic_Chapter_Id(userId, chapterId);
        
        Map<Long, Boolean> detailedProgress = new HashMap<>();

        for (ContentProgress progress : progressList) {
            detailedProgress.put(progress.getContentItem().getId(), progress.isCompleted());
        }
        
        return detailedProgress;
    }

    @Override
    @Transactional(readOnly = true)
    public ChapterProgressResponseDto getChapterProgress(Long userId, Long chapterId) {
        log.debug("Fetching chapter progress for user {} chapter {}", userId, chapterId);
        
        Enrollment enrollment = enrollmentRepository.findByUserIdAndChapterId(userId, chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("learner.enrollment.notfound"));

        return mapToChapterProgressDto(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentChapterProgressDto> getChapterStudentsProgress(Long chapterId) {
        log.debug("Fetching all students progress for chapter {}", chapterId);
        
        return enrollmentRepository.findByChapterId(chapterId).stream()
                .map(this::mapToStudentChapterProgressDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterProgressResponseDto> getAllEnrolledChapterProgress(Long userId) {
        log.debug("Fetching all enrolled chapter progress for user {}", userId);

        return enrollmentRepository.findByUserId(userId).stream()
                .map(this::mapToChapterProgressDto)
                .collect(Collectors.toList());
    }

    private void updateEnrollmentProgress(Long userId, Long chapterId, ContentItem latestCompletedItem) {
        enrollmentRepository.findByUserIdAndChapterId(userId, chapterId).ifPresent(enrollment -> {
            long totalItems = contentItemRepository.countByTopic_Chapter_Id(chapterId);

            if (totalItems == 0) return;

            long completedItems = progressRepository.countByUserIdAndContentItem_Topic_Chapter_IdAndIsCompletedTrue(userId, chapterId);
            
            double percentage = (double) completedItems / totalItems * 100;
            enrollment.setProgressPercentage(percentage);
            enrollment.setCompleted(completedItems == totalItems);

            // Update pointer
            if (enrollment.getLastCompletedContent() == null || isFurther(latestCompletedItem, enrollment.getLastCompletedContent())) {
                enrollment.setLastCompletedContent(latestCompletedItem);
                log.debug("Updated enrollment pointer for user {} to content {}", userId, latestCompletedItem.getId());
            }
            
            enrollmentRepository.save(enrollment);

            log.info("Updated enrollment progress for user {} chapter {}: {}%", userId, chapterId, percentage);
        });
    }

    private boolean isFurther(ContentItem current, ContentItem existing) {
        int currentTopicOrder = current.getTopic().getOrderIndex();
        int existingTopicOrder = existing.getTopic().getOrderIndex();

        if (currentTopicOrder > existingTopicOrder) return true;
        if (currentTopicOrder < existingTopicOrder) return false;

        // Same topic, compare content order
        return current.getOrderIndex() > existing.getOrderIndex();
    }

    private ContentProgress getOrCreateProgress(Long userId, Long contentItemId) {
        return progressRepository.findByUserIdAndContentItemId(userId, contentItemId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("auth.user.notfound"));

                    ContentItem contentItem = contentItemRepository.findById(contentItemId)
                            .orElseThrow(() -> new ResourceNotFoundException("course.content.notfound"));

                    ContentProgress newProgress = new ContentProgress();
                    newProgress.setUser(user);
                    newProgress.setContentItem(contentItem);
                    newProgress.setLastAccessed(LocalDateTime.now());

                    return newProgress;
                });
    }

    private ContentProgressResponseDto mapToContentProgressDto(ContentProgress progress) {
        return ContentProgressResponseDto.builder()
                .userId(progress.getUser().getId())
                .fullName(progress.getUser().getFullName())
                .contentItemId(progress.getContentItem().getId())
                .contentTitle(progress.getContentItem().getTitle())
                .contentType(progress.getContentItem().getType())
                .isCompleted(progress.isCompleted())
                .quizScore(progress.getQuizScore())
                .build();
    }

    private ChapterProgressResponseDto mapToChapterProgressDto(Enrollment enrollment) {
        return ChapterProgressResponseDto.builder()
                .chapterId(enrollment.getChapter().getId())
                .chapterTitle(enrollment.getChapter().getTitle())
                .progressPercentage(enrollment.getProgressPercentage())
                .isCompleted(enrollment.isCompleted())
                .build();
    }

    private StudentChapterProgressDto mapToStudentChapterProgressDto(Enrollment enrollment) {
        return StudentChapterProgressDto.builder()
                .studentId(enrollment.getUser().getId())
                .studentName(enrollment.getUser().getFullName())
                .progressPercentage(enrollment.getProgressPercentage())
                .isCompleted(enrollment.isCompleted())
                .build();
    }
}
