package com.montola.school.course.controller;

import com.montola.school.course.dto.SubjectRequestDto;
import com.montola.school.course.dto.SubjectResponseDto;
import com.montola.school.course.service.SubjectService;
import com.montola.school.course.dto.structure.SubjectStructureResponseDto;
import com.montola.school.course.service.CourseStructureService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author avidewan
 * @date 11/30/25
 */
@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
@Tag(name = "Subject Management", description = "Endpoints to manage subjects")
@Slf4j
public class SubjectController {

    private final SubjectService subjectService;
    private final CourseStructureService courseStructureService;

    @Operation(summary = "Create a new subject")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<SubjectResponseDto> createSubject(@Valid @RequestBody SubjectRequestDto dto) {
        log.info("Creating new subject: {}", dto.getName());
        SubjectResponseDto createdSubject = subjectService.create(dto);
        log.info("Subject created with id: {}", createdSubject.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdSubject);
    }

    @Operation(summary = "Get all subjects")
    @GetMapping
    public ResponseEntity<List<SubjectResponseDto>> getAllSubjects() {
        log.info("Fetching all subjects");
        List<SubjectResponseDto> subjects = subjectService.getAll();
        log.debug("Total subjects found: {}", subjects.size());

        return ResponseEntity.ok(subjects);
    }

    @Operation(summary = "Get subject by id")
    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponseDto> getSubjectById(@PathVariable Long id) {
        log.info("Fetching subject by id: {}", id);

        return subjectService.getById(id)
                .map(subjectDto -> {
                    log.debug("Subject found with id {}", id);

                    return ResponseEntity.ok(subjectDto);
                })
                .orElseGet(() -> {
                    log.warn("Subject not found with id {}", id);

                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @Operation(summary = "Update a subject")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<SubjectResponseDto> updateSubject(@PathVariable Long id,
                                                            @Valid @RequestBody SubjectRequestDto dto) {
        log.info("Updating subject with id: {}", id);
        SubjectResponseDto updatedSubject = subjectService.update(id, dto);
        log.info("Subject updated with id: {}", updatedSubject.getId());

        return ResponseEntity.ok(updatedSubject);
    }

    @Operation(summary = "Delete a subject")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        log.info("Deleting subject with id: {}", id);
        subjectService.delete(id);
        log.info("Subject deleted with id: {}", id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get full course structure for a subject (Tree View)")
    @GetMapping("/{id}/structure")
    public ResponseEntity<SubjectStructureResponseDto> getSubjectStructure(@PathVariable Long id) {
        log.info("Fetching structure for subject id: {}", id);

        return ResponseEntity.ok(courseStructureService.getSubjectStructure(id));
    }
}
