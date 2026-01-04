package com.montola.school.course.controller;

import com.montola.school.course.dto.ChapterRequestDto;
import com.montola.school.course.dto.ChapterResponseDto;
import com.montola.school.course.service.ChapterService;
import com.montola.school.course.dto.structure.ChapterStructureResponseDto;
import com.montola.school.course.service.CourseStructureService;
import com.montola.school.course.service.ChapterAuthorizationService;
import com.montola.school.auth.security.CustomUserDetails;
import com.montola.school.course.enums.ChapterStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
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
        ChapterResponseDto updatedChapter = chapterService.update(id, dto); //TODO: Change ut to patch . System design maybe we should remove status from here. Have a separate method to change the status
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

    @Operation(summary = "Update chapter status")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ChapterResponseDto> updateStatus(@PathVariable Long id, @RequestParam ChapterStatus status) {
        log.info("Updating status for chapter {} to {}", id, status);
        return ResponseEntity.ok(chapterService.updateStatus(id, status));
    }

    @Operation(summary = "Toggle chapter free status")
    @PatchMapping("/{id}/free-status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ChapterResponseDto> toggleFreeStatus(@PathVariable Long id, @RequestParam boolean isFree) {
        log.info("Toggling free status for chapter {} to {}", id, isFree);
        return ResponseEntity.ok(chapterService.toggleFreeStatus(id, isFree));
    }

    @Operation(summary = "Upload chapter cover image")
    @PostMapping(value = "/{id}/cover-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> uploadCoverImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        log.info("Uploading cover image for chapter {}", id);

        try {
            if (file.getSize() > 500 * 1024) {
                 throw new IllegalArgumentException("File size exceeds 500KB limit");
            }

            chapterService.uploadCoverImage(id, file.getBytes());

            return ResponseEntity.ok().build();

        } catch (IOException e) {
            log.error("Failed to read file", e);
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Operation(summary = "Get chapter cover image")
    @GetMapping("/{id}/cover-image")
    public ResponseEntity<byte[]> getCoverImage(@PathVariable Long id) {
        log.info("Fetching cover image for chapter {}", id);
        byte[] image = chapterService.getCoverImage(id);

        if (image == null || image.length == 0) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setCacheControl("max-age=31536000"); // Cache for 1 year

        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}
