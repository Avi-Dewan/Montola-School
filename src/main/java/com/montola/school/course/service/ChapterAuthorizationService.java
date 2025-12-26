package com.montola.school.course.service;

import com.montola.school.auth.enums.Role;
import com.montola.school.auth.model.User;
import com.montola.school.auth.repository.UserRepository;
import com.montola.school.course.model.ContentItem;
import com.montola.school.course.model.Topic;
import com.montola.school.course.repository.ChapterTeacherRepository;
import com.montola.school.course.repository.ContentItemRepository;
import com.montola.school.course.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for checking chapter-level authorization.
 *
 * @author avidewan
 * @date 12/15/2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChapterAuthorizationService {

    private final ChapterTeacherRepository chapterTeacherRepository;
    private final TopicRepository topicRepository;
    private final ContentItemRepository contentItemRepository;
    private final UserRepository userRepository;

    /**
     * Check if user can edit a chapter.
     * ADMIN/MANAGER can edit any chapter.
     * TEACHER can edit if assigned to the chapter.
     */
    public boolean canEditChapter(Long userId, Long chapterId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }

        // ADMIN/MANAGER have full access
        if (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.MANAGER)) {
            return true;
        }

        // TEACHER can edit if assigned
        if (user.getRoles().contains(Role.TEACHER)) {
            return chapterTeacherRepository.existsByChapterIdAndTeacherId(chapterId, userId);
        }

        return false;
    }

    /**
     * Check if user can edit a topic.
     * Checks authorization on the parent chapter.
     */
    public boolean canEditTopic(Long userId, Long topicId) {
        Topic topic = topicRepository.findById(topicId).orElse(null);
        if (topic == null) {
            return false;
        }

        return canEditChapter(userId, topic.getChapter().getId());
    }

    /**
     * Check if user can edit content.
     * Checks authorization on the parent chapter (via topic).
     */
    public boolean canEditContent(Long userId, Long contentItemId) {
        ContentItem contentItem = contentItemRepository.findById(contentItemId).orElse(null);
        if (contentItem == null) {
            return false;
        }

        return canEditTopic(userId, contentItem.getTopic().getId());
    }

    /**
     * Check if user is ADMIN or MANAGER.
     */
    public boolean isAdminOrManager(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }

        return user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.MANAGER);
    }
}
