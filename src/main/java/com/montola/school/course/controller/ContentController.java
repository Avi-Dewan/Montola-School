package com.montola.school.course.controller;

import com.montola.school.auth.model.User;
import com.montola.school.auth.service.UserService;
import com.montola.school.course.dto.*;
import com.montola.school.course.service.*;
import com.montola.school.auth.security.CustomUserDetails;
import com.montola.school.common.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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

    @Operation(summary = "Get content by ID (with enrollment check)",
            description = "Retrieves the content details (Lecture, Quiz, or PDF) by its ID. " +
                    "Checks if the user is enrolled in the course/chapter if it's not free. " +
                    "Also enforces sequential access if applicable (previous content must be completed).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Content retrieved successfully",
                    content = @Content(schema = @Schema(oneOf = {
                            LectureResponseDto.class,
                            QuizResponseDto.class,
                            GooglePdfContentResponseDto.class
                    }))),
            @ApiResponse(responseCode = "403", description = "Access denied. Reasons: \n" +
                    "- User is not enrolled in the chapter/course (content.purchase.toAccess)\n" +
                    "- Previous content in the sequence is not completed (content.complete.previous)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Content item not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getContentById(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        log.info("User {} requesting content {}", currentUser.getId(), id);

        Object content = contentAccessService.getContentById(id, currentUser.getId(), currentUser.isAdminOrManagerOrTeacher());

        return ResponseEntity.ok(content);
    }

    @Operation(summary = "Get the PDF file for a content item (with enrollment check)",
            description = "Streams the PDF with the reader's watermark stamped in. Enforces the same " +
                    "enrollment and sequential-access rules as the content endpoint, so the file is " +
                    "only ever handed over after the metadata would have been readable.")
    @GetMapping("/{id}/file")
    public ResponseEntity<byte[]> getPdfFile(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        log.info("User {} requesting PDF file for content {}", currentUser.getId(), id);

        byte[] file = contentAccessService.getPdfFile(id, currentUser.getId(), currentUser.isAdminOrManagerOrTeacher());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"content-" + id + ".pdf\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(file);
    }

    @Operation(summary = "Upload the file for a PDF content item",
            description = "Stores the document in the application's object storage and serves it from " +
                    "there, instead of an external reference such as a Google Drive file id.")
    @PostMapping("/{id}/file")
    public ResponseEntity<GooglePdfContentResponseDto> uploadPdfFile(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                                     @PathVariable Long id,
                                                                     @RequestParam("file") MultipartFile file) {
        log.info("User {} uploading a PDF file for content {}", currentUser.getId(), id);

        if (!authorizationService.canEditContent(currentUser.getId(), id)) {
            throw new AccessDeniedException("Insufficient permissions to upload content");
        }

        return ResponseEntity.ok(googlePdfContentService.uploadFile(
                id, readBytes(file), file.getOriginalFilename(), file.getContentType()));
    }

    @Operation(summary = "Upload the video for a lecture content item",
            description = "Stores the video in the application's object storage and serves it through a " +
                    "short-lived signed URL, instead of an embeddable YouTube id.")
    @PostMapping("/{id}/video")
    public ResponseEntity<LectureResponseDto> uploadLectureVideo(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                                 @PathVariable Long id,
                                                                 @RequestParam("file") MultipartFile file) {
        log.info("User {} uploading a video for content {}", currentUser.getId(), id);

        if (!authorizationService.canEditContent(currentUser.getId(), id)) {
            throw new AccessDeniedException("Insufficient permissions to upload content");
        }

        return ResponseEntity.ok(lectureService.uploadVideo(
                id, readBytes(file), file.getOriginalFilename(), file.getContentType()));
    }

    private static byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read the uploaded file.", e);
        }
    }

    @Operation(summary = "Update an existing lecture by content item ID")
    @PutMapping("/lecture/content-item/{contentItemId}")
    public ResponseEntity<?> updateLectureByContentItem(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                        @PathVariable Long contentItemId,
                                                        @RequestBody LectureRequestDto dto) {
        log.info("Updating lecture for content item {} by user {}", contentItemId, currentUser.getId());

        if (!authorizationService.canEditTopic(currentUser.getId(), dto.getTopicId())) {
            throw new AccessDeniedException("Insufficient permissions to update lecture");
        }

        return ResponseEntity.ok(lectureService.updateByContentItemId(contentItemId, dto));
    }

    @Operation(summary = "Delete a lecture by content item ID")
    @DeleteMapping("/lecture/content-item/{contentItemId}")
    public ResponseEntity<?> deleteLectureByContentItem(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                        @PathVariable Long contentItemId) {
        log.info("Deleting lecture for content item {} by user {}", contentItemId, currentUser.getId());

        var lecture = lectureService.getByContentItemId(contentItemId);

        if (!authorizationService.canEditTopic(currentUser.getId(), lecture.getTopicId())) {
             throw new AccessDeniedException("Insufficient permissions to delete lecture");
        }

        lectureService.deleteByContentItemId(contentItemId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update an existing quiz")
    @PutMapping("/quiz/{id}")
    public ResponseEntity<?> updateQuiz(@AuthenticationPrincipal CustomUserDetails currentUser,
                                        @PathVariable Long id,
                                        @RequestBody QuizRequestDto dto) {

        log.info("Updating quiz {} by user {}", id, currentUser.getId());

        // Authorization check
        if (!authorizationService.canEditTopic(currentUser.getId(), dto.getTopicId())) {
            throw new AccessDeniedException("Insufficient permissions to update quiz");
        }

        return ResponseEntity.ok(quizService.update(id, dto));
    }

    @Operation(summary = "Update quiz questions")
    @PutMapping("/quiz/{id}/questions")
    public ResponseEntity<?> updateQuizQuestions(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                  @PathVariable Long id,
                                                  @RequestBody List<QuizQuestionRequestDto> questions) {

        log.info("Updating questions for quiz {} by user {}", id, currentUser.getId());

        // Authorization check - potentially need a better way to check topic of a quiz by quiz id
        // For now, let's assume we can fetch it or use a broader check if available.
        // To be safe, let's fetch the quiz to get the topic ID for authorization.
        var quiz = quizService.getById(id);

        if (!authorizationService.canEditTopic(currentUser.getId(), quiz.getTopicId())) {
            throw new AccessDeniedException("Insufficient permissions to update quiz questions");
        }

        return ResponseEntity.ok(quizService.updateQuestions(id, questions));
    }

    @Operation(summary = "Update an existing quiz by content item ID")
    @PutMapping("/quiz/content-item/{contentItemId}")
    public ResponseEntity<?> updateQuizByContentItem(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                     @PathVariable Long contentItemId,
                                                     @RequestBody QuizRequestDto dto) {

        log.info("Updating quiz for content item {} by user {}", contentItemId, currentUser.getId());

        // Authorization check
        if (!authorizationService.canEditTopic(currentUser.getId(), dto.getTopicId())) {
            throw new AccessDeniedException("Insufficient permissions to update quiz");
        }

        return ResponseEntity.ok(quizService.updateByContentItemId(contentItemId, dto));
    }

    @Operation(summary = "Delete a quiz by content item ID")
    @DeleteMapping("/quiz/content-item/{contentItemId}")
    public ResponseEntity<?> deleteQuizByContentItem(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                     @PathVariable Long contentItemId) {
        log.info("Deleting quiz for content item {} by user {}", contentItemId, currentUser.getId());

        var quiz = quizService.getByContentItemId(contentItemId);
        if (!authorizationService.canEditTopic(currentUser.getId(), quiz.getTopicId())) {
            throw new AccessDeniedException("Insufficient permissions to delete quiz");
        }

        quizService.deleteByContentItemId(contentItemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update quiz questions by content item ID")
    @PutMapping("/quiz/content-item/{contentItemId}/questions")
    public ResponseEntity<?> updateQuizQuestionsByContentItem(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                               @PathVariable Long contentItemId,
                                                               @RequestBody List<QuizQuestionRequestDto> questions) {

        log.info("Updating questions for quiz with content item {} by user {}", contentItemId, currentUser.getId());

        // Authorization check
        var quiz = quizService.getByContentItemId(contentItemId);
        if (!authorizationService.canEditTopic(currentUser.getId(), quiz.getTopicId())) {
            throw new AccessDeniedException("Insufficient permissions to update quiz questions");
        }

        return ResponseEntity.ok(quizService.updateQuestionsByContentItemId(contentItemId, questions));
    }

    @Operation(summary = "Update an existing Google PDF content by content item ID")
    @PutMapping("/pdf/content-item/{contentItemId}")
    public ResponseEntity<?> updateGooglePdfContentByContentItem(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                                 @PathVariable Long contentItemId,
                                                                 @RequestBody GooglePdfContentRequestDto dto) {
        log.info("Updating Google PDF content for content item {} by user {}", contentItemId, currentUser.getId());

        if (!authorizationService.canEditTopic(currentUser.getId(), dto.getTopicId())) {
            throw new AccessDeniedException("Insufficient permissions to update Google PDF content");
        }

        return ResponseEntity.ok(googlePdfContentService.updateByContentItemId(contentItemId, dto));
    }

    @Operation(summary = "Delete a Google PDF content by content item ID")
    @DeleteMapping("/pdf/content-item/{contentItemId}")
    public ResponseEntity<?> deleteGooglePdfContentByContentItem(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                                 @PathVariable Long contentItemId) {
        log.info("Deleting Google PDF content for content item {} by user {}", contentItemId, currentUser.getId());

        var pdf = googlePdfContentService.getByContentItemId(contentItemId);

        if (!authorizationService.canEditTopic(currentUser.getId(), pdf.getTopicId())) {
            throw new AccessDeniedException("Insufficient permissions to delete Google PDF content");
        }

        googlePdfContentService.deleteByContentItemId(contentItemId);

        return ResponseEntity.noContent().build();
    }
}