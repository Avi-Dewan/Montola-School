package com.montola.school.payment.service;

import com.montola.school.auth.model.User;
import com.montola.school.auth.repository.UserRepository;
import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.course.model.Chapter;
import com.montola.school.course.repository.ChapterRepository;
import com.montola.school.learner.model.Enrollment;
import com.montola.school.learner.repository.EnrollmentRepository;
import com.montola.school.payment.dto.EnrollmentResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of FreeEnrollmentService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FreeEnrollmentServiceImpl implements FreeEnrollmentService {

    private final ChapterRepository chapterRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EnrollmentResponseDto enrollInFreeChapter(Long userId, Long chapterId) {
        log.info("Request to enroll in free chapter {} by user {}", chapterId, userId);

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("course.chapter.notfound"));

        if (!chapter.isFree()) {
            throw new IllegalArgumentException("Chapter is not free");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("auth.user.notfound"));

        if (enrollmentRepository.existsByUserIdAndChapterId(userId, chapterId)) {
            log.warn("User {} is already enrolled in chapter {}", userId, chapterId);
            Enrollment existingEnrollment = enrollmentRepository.findByUserIdAndChapterId(userId, chapterId)
                    .orElseThrow(() -> new ResourceNotFoundException("enrollment.notfound"));
            return mapToDto(existingEnrollment);
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setChapter(chapter);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setProgressPercentage(0.0);
        enrollment.setCompleted(false);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        log.info("Successfully enrolled user {} in free chapter {}", userId, chapterId);

        return mapToDto(savedEnrollment);
    }

    private EnrollmentResponseDto mapToDto(Enrollment enrollment) {
        return EnrollmentResponseDto.builder()
                .enrollmentId(enrollment.getId())
                .userId(enrollment.getUser().getId())
                .chapterId(enrollment.getChapter().getId())
                .chapterTitle(enrollment.getChapter().getTitle())
                .enrolledAt(enrollment.getEnrolledAt())
                .status("ENROLLED")
                .build();
    }
}
