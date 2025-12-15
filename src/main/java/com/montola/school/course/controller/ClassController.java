package com.montola.school.course.controller;

import com.montola.school.course.dto.ClassRequestDto;
import com.montola.school.course.dto.ClassResponseDto;
import com.montola.school.course.service.ClassService;
import com.montola.school.course.dto.structure.ClassStructureResponseDto;
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
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
@Tag(name = "Class Management", description = "Endpoints to manage classes")
@Slf4j
public class ClassController {

    private final ClassService classService;
    private final CourseStructureService courseStructureService;

    @Operation(summary = "Create a new class")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ClassResponseDto> createClass(@Valid @RequestBody ClassRequestDto dto) {
        log.info("Creating new class: {}", dto.getName());
        ClassResponseDto createdClass = classService.create(dto);
        log.info("Class created with id: {}", createdClass.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdClass);
    }

    @Operation(summary = "Get all classes")
    @GetMapping
    public ResponseEntity<List<ClassResponseDto>> getAllClasses() {
        log.info("Fetching all classes");
        List<ClassResponseDto> classes = classService.getAll();
        log.debug("Total classes found: {}", classes.size());
        return ResponseEntity.ok(classes);
    }

    @Operation(summary = "Get class by id")
    @GetMapping("/{id}")
    public ResponseEntity<ClassResponseDto> getClassById(@PathVariable Long id) {
        log.info("Fetching class by id: {}", id);
        return classService.getById(id)
                .map(classDto -> {
                    log.debug("Class found with id {}", id);
                    return ResponseEntity.ok(classDto);
                })
                .orElseGet(() -> {
                    log.warn("Class not found with id {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @Operation(summary = "Update a class")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ClassResponseDto> updateClass(@PathVariable Long id, @Valid @RequestBody ClassRequestDto dto) {
        log.info("Updating class with id: {}", id);
        ClassResponseDto updatedClass = classService.update(id, dto);
        log.info("Class updated with id: {}", updatedClass.getId());
        return ResponseEntity.ok(updatedClass);
    }

    @Operation(summary = "Delete a class")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        log.info("Deleting class with id: {}", id);
        classService.delete(id);
        log.info("Class deleted with id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get full course structure for a class (Tree View)")
    @GetMapping("/{id}/structure")
    public ResponseEntity<ClassStructureResponseDto> getClassStructure(@PathVariable Long id) {
        log.info("Fetching structure for class id: {}", id);

        return ResponseEntity.ok(courseStructureService.getClassStructure(id));
    }
}
