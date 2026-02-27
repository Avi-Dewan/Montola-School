package com.montola.school.payment.controller;

import com.montola.school.auth.security.CustomUserDetails;
import com.montola.school.payment.dto.EnrollmentResponseDto;
import com.montola.school.payment.service.FreeEnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for free chapter enrollments.
 */
@RestController
@RequestMapping("/api/v1/enrollments/free")
@RequiredArgsConstructor
@Slf4j
public class FreeEnrollmentController {

    private final FreeEnrollmentService freeEnrollmentService;

    @PostMapping("/{chapterId}")
    public ResponseEntity<EnrollmentResponseDto> enrollInFreeChapter(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                                     @PathVariable Long chapterId) {
        log.info("Request to enroll in free chapter {} by user {}", chapterId, currentUser.getId());
        EnrollmentResponseDto enrollment = freeEnrollmentService.enrollInFreeChapter(currentUser.getId(), chapterId);
        return ResponseEntity.ok(enrollment);
    }
}
