package com.montola.school.learner.controller;

import com.montola.school.auth.security.CustomUserDetails;
import com.montola.school.learner.model.ContentProgress;
import com.montola.school.learner.service.ProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Controller for tracking content progress.
 *
 * @author avidewan
 * @date 12/13/2025
 */
@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
@Slf4j
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping("/content/{contentItemId}/complete")
    public ResponseEntity<ContentProgress> markComplete(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                        @PathVariable Long contentItemId) {

        log.info("Request to mark content {} complete by user {}", contentItemId, currentUser.getId());
        ContentProgress progress = progressService.markComplete(currentUser.getId(), contentItemId);

        return ResponseEntity.ok(progress);
    }

    @PostMapping("/content/{contentItemId}/quiz")
    public ResponseEntity<ContentProgress> submitQuizScore(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                           @PathVariable Long contentItemId,
                                                           @RequestBody Map<String, Double> payload) {
        Double score = payload.get("score");
        log.info("Request to submit quiz score {} for content {} by user {}", score, contentItemId, currentUser.getId());

        ContentProgress progress = progressService.submitQuizResult(currentUser.getId(), contentItemId, score);

        return ResponseEntity.ok(progress);
    }

    @GetMapping("/my-progress")
    public ResponseEntity<List<ContentProgress>> getMyProgress(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(progressService.getStudentProgress(currentUser.getId()));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<ContentProgress>> getStudentProgress(@PathVariable Long studentId) {
        return ResponseEntity.ok(progressService.getStudentProgress(studentId));
    }
}
