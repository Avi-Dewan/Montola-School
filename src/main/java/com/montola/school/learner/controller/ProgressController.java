package com.montola.school.learner.controller;

import com.montola.school.auth.security.CustomUserDetails;
import com.montola.school.learner.dto.ChapterProgressResponseDto;
import com.montola.school.learner.dto.ContentProgressResponseDto;
import com.montola.school.learner.service.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for tracking content progress.
 *
 * @author avidewan
 * @date 12/13/2025
 */
@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
@Tag(name = "Progress Management", description = "Endpoints to track and view student progress")
@Slf4j
public class ProgressController {

    private final ProgressService progressService;

    @Operation(summary = "Mark a content item as complete")
    @PostMapping("/content/{contentItemId}/complete")
    public ResponseEntity<ContentProgressResponseDto> markComplete(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                                   @PathVariable Long contentItemId) {

        log.info("Request to mark content {} complete by user {}", contentItemId, currentUser.getId());

        return ResponseEntity.ok(progressService.markComplete(currentUser.getId(), contentItemId));
    }

    @Operation(summary = "Submit quiz score and mark as complete")
    @PostMapping("/content/{contentItemId}/quiz")
    public ResponseEntity<ContentProgressResponseDto> submitQuizScore(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                                      @PathVariable Long contentItemId,
                                                                      @RequestBody Map<String, Double> payload) {
        Double score = payload.get("score");
        log.info("Request to submit quiz score {} for content {} by user {}", score, contentItemId, currentUser.getId());

        return ResponseEntity.ok(progressService.submitQuizResult(currentUser.getId(), contentItemId, score));
    }

    @Operation(summary = "Get list of all content progress for current student") // TODO: delete  ?
    @GetMapping("/my-content-progress")
    public ResponseEntity<List<ContentProgressResponseDto>> getMyContentProgress(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(progressService.getStudentProgress(currentUser.getId()));
    }

    @Operation(summary = "Get progress summary for a specific chapter")
    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<ChapterProgressResponseDto> getChapterProgress(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                                         @PathVariable Long chapterId) {
        return ResponseEntity.ok(progressService.getChapterProgress(currentUser.getId(), chapterId));
    }

    @Operation(summary = "Get summary of progress for all enrolled chapters")
    @GetMapping("/my-chapters")
    public ResponseEntity<List<ChapterProgressResponseDto>> getMyChaptersProgress(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(progressService.getAllEnrolledChapterProgress(currentUser.getId()));
    }

    @Operation(summary = "Get progress for a specific student (Admin/Manager only)")
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")  // TODO: delete ? what will admin or manager do with individual contnet progress
    public ResponseEntity<List<ContentProgressResponseDto>> getStudentProgress(@PathVariable Long studentId) {
        return ResponseEntity.ok(progressService.getStudentProgress(studentId));
    }
}
