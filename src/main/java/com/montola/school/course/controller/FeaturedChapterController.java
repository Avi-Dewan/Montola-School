package com.montola.school.course.controller;

import com.montola.school.course.dto.FeaturedChapterResponseDto;
import com.montola.school.course.service.FeaturedChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing Featured Chapters.
 *
 * @author avidewan
 * @date 1/4/26
 */
@RestController
@RequestMapping("/api/v1/featured-chapters")
@RequiredArgsConstructor
@Tag(name = "Featured Chapters", description = "Endpoints to manage and view featured chapters")
@Slf4j
public class FeaturedChapterController {

    private final FeaturedChapterService featuredChapterService;

    @Operation(summary = "Get all featured chapters (Public)")
    @GetMapping
    public ResponseEntity<List<FeaturedChapterResponseDto>> getFeaturedChapters() {
        log.info("Fetching all featured chapters");
        List<FeaturedChapterResponseDto> featured = featuredChapterService.getFeaturedChapters();

        return ResponseEntity.ok(featured);
    }

    @Operation(summary = "Add a chapter to featured list (Admin/Manager)")
    @PostMapping("/{chapterId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> addFeaturedChapter(@PathVariable Long chapterId) {
        log.info("Request to feature chapter {}", chapterId);
        featuredChapterService.addFeaturedChapter(chapterId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove a chapter from featured list (Admin/Manager)")
    @DeleteMapping("/{chapterId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> removeFeaturedChapter(@PathVariable Long chapterId) {
        log.info("Request to remove featured chapter {}", chapterId);
        featuredChapterService.removeFeaturedChapter(chapterId);

        return ResponseEntity.ok().build();
    }
}
