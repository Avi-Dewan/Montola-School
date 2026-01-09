package com.montola.school.course.controller;

import com.montola.school.auth.security.CustomUserDetails;
import com.montola.school.course.dto.ChapterResponseDto;
import com.montola.school.course.dto.ChapterStatisticsDto;
import com.montola.school.course.service.ChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for teacher-specific operations.
 *
 * @author avidewan
 * @date 1/5/26
 */
@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
@Tag(name = "Teacher Management", description = "Endpoints for teacher operations")
@Slf4j
public class TeacherController {

    private final ChapterService chapterService;

    @Operation(summary = "Get chapters assigned to the current teacher")
    @GetMapping("/assigned-chapters")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<ChapterResponseDto>> getAssignedChapters(@AuthenticationPrincipal CustomUserDetails currentUser) {
        log.info("Fetching assigned chapters for teacher {}", currentUser.getId());
        List<ChapterResponseDto> chapters = chapterService.getAssignedChapters(currentUser.getId());
        log.debug("Found {} assigned chapters", chapters.size());

        return ResponseEntity.ok(chapters);
    }

    @Operation(summary = "Get statistics for a chapter")
    @GetMapping("/chapters/{chapterId}/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<ChapterStatisticsDto> getChapterStatistics(@PathVariable Long chapterId) {
        log.info("Fetching statistics for chapter {}", chapterId);
        ChapterStatisticsDto stats = chapterService.getChapterStatistics(chapterId);

        return ResponseEntity.ok(stats);
    }
}
