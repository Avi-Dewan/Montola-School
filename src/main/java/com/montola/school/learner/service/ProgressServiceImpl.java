package com.montola.school.learner.service;

import com.montola.school.auth.model.User;
import com.montola.school.auth.repository.UserRepository;
import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.course.model.ContentItem;
import com.montola.school.course.repository.ContentItemRepository;
import com.montola.school.learner.model.ContentProgress;
import com.montola.school.learner.repository.ContentProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    @Override
    @Transactional
    public ContentProgress markComplete(Long userId, Long contentItemId) {
        log.info("Marking content {} as complete for user {}", contentItemId, userId);

        ContentProgress progress = getOrCreateProgress(userId, contentItemId);
        progress.setCompleted(true);
        progress.setLastAccessed(LocalDateTime.now());

        return progressRepository.save(progress);
    }

    @Override
    @Transactional
    public ContentProgress submitQuizResult(Long userId, Long contentItemId, Double score) {
        log.info("Submitting quiz score {} for content {} user {}", score, contentItemId, userId);

        ContentProgress progress = getOrCreateProgress(userId, contentItemId);
        progress.setQuizScore(score);
        progress.setLastAccessed(LocalDateTime.now());
        // Logic: If they pass the quiz, maybe mark complete? 
        // For now, we'll assume submitting a score constitutes completion or logic handled elsewhere.
        // Let's just track the score.
        progress.setCompleted(true); // Assuming taking the quiz marks it as "done" attempt-wise.

        return progressRepository.save(progress);
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
}
