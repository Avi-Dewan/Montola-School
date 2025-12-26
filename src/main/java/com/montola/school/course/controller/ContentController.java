package com.montola.school.course.controller;

import com.montola.school.auth.model.User;
import com.montola.school.auth.service.UserService;
import com.montola.school.course.dto.GooglePdfContentRequestDto;
import com.montola.school.course.dto.LectureRequestDto;
import com.montola.school.course.dto.QuizRequestDto;
import com.montola.school.course.service.*;
import com.montola.school.auth.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author avidewan
 * @date 11/30/25
 */
@RestController
@RequestMapping("/api/v1/contents")
@RequiredArgsConstructor
@Tag(name = "Content Management", description = "Endpoints to manage content items")
@Slf4j
public class ContentController {

    private final LectureService lectureService;
    private final QuizService quizService;
    private final GooglePdfContentService googlePdfContentService;
    private final ChapterAuthorizationService authorizationService;
    private final ContentAccessService contentAccessService;
    private final UserService userService;

    @Operation(summary = "Create a new lecture")
    @PostMapping("/lecture")
    public ResponseEntity<?> createLecture(@AuthenticationPrincipal CustomUserDetails currentUser,
                                           @RequestBody LectureRequestDto dto) {
        log.info("Creating new lecture by user {}", currentUser.getId());

        // Authorization check to check can edit topic of the content
        if (!authorizationService.canEditTopic(currentUser.getId(), dto.getTopicId())) {
            throw new AccessDeniedException("Insufficient permissions to create lecture");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(lectureService.create(dto));
    }

    @Operation(summary = "Create a new quiz")
    @PostMapping("/quiz")
    public ResponseEntity<?> createQuiz(@AuthenticationPrincipal CustomUserDetails currentUser,
                                        @RequestBody QuizRequestDto dto) {
        log.info("Creating new quiz by user {}", currentUser.getId());

        // Authorization check to check can edit topic of the content
        if (!authorizationService.canEditTopic(currentUser.getId(), dto.getTopicId())) {
            throw new AccessDeniedException("Insufficient permissions to create lecture");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(quizService.create(dto));
    }

    @Operation(summary = "Create a new Google PDF content")
    @PostMapping("/pdf")
    public ResponseEntity<?> createGooglePdfContent(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                    @RequestBody GooglePdfContentRequestDto dto) {
        log.info("Creating new Google PDF content by user {}", currentUser.getId());

        // Authorization check to check can edit topic of the content
        if (!authorizationService.canEditTopic(currentUser.getId(), dto.getTopicId())) {
            throw new AccessDeniedException("Insufficient permissions to create lecture");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(googlePdfContentService.create(dto));
    }

    @Operation(summary = "Get content by ID (with enrollment check)")
    @GetMapping("/{id}")
    public ResponseEntity<?> getContentById(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        log.info("User {} requesting content {}", currentUser.getId(), id);

        Object content = contentAccessService.getContentById(id, currentUser.getId(), currentUser.isAdminOrManagerOrTeacher());

        return ResponseEntity.ok(content);
    }
}
