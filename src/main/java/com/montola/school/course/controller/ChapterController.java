package com.montola.school.course.controller;

import com.montola.school.course.dto.ChapterRequestDto;
import com.montola.school.course.dto.ChapterResponseDto;
import com.montola.school.course.service.ChapterService;
import com.montola.school.course.dto.structure.ChapterStructureResponseDto;
import com.montola.school.course.service.CourseStructureService;
import com.montola.school.course.service.ChapterAuthorizationService;
import com.montola.school.auth.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author avidewan
 * @date 11/30/25
 */
@RestController
@RequestMapping("/api/v1/chapters")
@RequiredArgsConstructor
@Tag(name = "Chapter Management", description = "Endpoints to manage chapters")
@Slf4j
public class ChapterController {

    private final ChapterService chapterService;
    private final CourseStructureService courseStructureService;
    private final ChapterAuthorizationService authorizationService;

    @Operation(summary = "Create a new chapter")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ChapterResponseDto> createChapter(@Valid @RequestBody ChapterRequestDto dto) {
        log.info("Creating new chapter: {}", dto.getTitle());
        ChapterResponseDto createdChapter = chapterService.create(dto);
        log.info("Chapter created with id: {}", createdChapter.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdChapter);
    }

    @Operation(summary = "Get all chapters")
    @GetMapping
    public ResponseEntity<List<ChapterResponseDto>> getAllChapters() {
        log.info("Fetching all chapters");
        List<ChapterResponseDto> chapters = chapterService.getAll();
        log.debug("Total chapters found: {}", chapters.size());

        return ResponseEntity.ok(chapters);
    }

    @Operation(summary = "Get chapter by id")
    @GetMapping("/{id}")
    public ResponseEntity<ChapterResponseDto> getChapterById(@PathVariable Long id) {
        log.info("Fetching chapter by id: {}", id);

        return chapterService.getById(id)
                .map(chapterDto -> {
                    log.debug("Chapter found with id {}", id);
                    return ResponseEntity.ok(chapterDto);
                })
                .orElseGet(() -> {
                    log.warn("Chapter not found with id {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @Operation(summary = "Update a chapter")
    @PutMapping("/{id}")
    public ResponseEntity<ChapterResponseDto> updateChapter(@PathVariable Long id, @Valid @RequestBody ChapterRequestDto dto) {
        log.info("Updating chapter with id: {}", id);
        ChapterResponseDto updatedChapter = chapterService.update(id, dto);
        log.info("Chapter updated with id: {}", updatedChapter.getId());

        return ResponseEntity.ok(updatedChapter);
    }

    @Operation(summary = "Delete a chapter")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> deleteChapter(@PathVariable Long id) {
        log.info("Deleting chapter with id: {}", id);
        chapterService.delete(id);
        log.info("Chapter deleted with id: {}", id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get full course structure for a chapter (Tree View)")
    @GetMapping("/{id}/structure")
    public ResponseEntity<ChapterStructureResponseDto> getChapterStructure(@PathVariable Long id) {
        log.info("Fetching structure for chapter id: {}", id);

        return ResponseEntity.ok(courseStructureService.getChapterStructure(id));
    }

    @Operation(summary = "Assign a teacher to a chapter")
    @PostMapping("/{chapterId}/assign-teacher")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> assignTeacher(@PathVariable Long chapterId,
                                               @RequestParam Long teacherId,
                                               @AuthenticationPrincipal CustomUserDetails currentUser) {
        log.info("Assigning teacher {} to chapter {} by user {}", teacherId, chapterId, currentUser.getId());
        chapterService.assignTeacher(chapterId, teacherId, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Unassign a teacher from a chapter")
    @DeleteMapping("/{chapterId}/teachers/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> unassignTeacher(@PathVariable Long chapterId,
                                                 @PathVariable Long teacherId) {
        log.info("Unassigning teacher {} from chapter {}", teacherId, chapterId);
        chapterService.unassignTeacher(chapterId, teacherId);
        return ResponseEntity.noContent().build();
    }
}
