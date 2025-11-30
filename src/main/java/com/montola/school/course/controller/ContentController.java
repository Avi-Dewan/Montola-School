package com.montola.school.course.controller;

import com.montola.school.course.dto.GooglePdfContentRequestDto;
import com.montola.school.course.dto.LectureRequestDto;
import com.montola.school.course.dto.QuizRequestDto;
import com.montola.school.course.service.GooglePdfContentService;
import com.montola.school.course.service.LectureService;
import com.montola.school.course.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Operation(summary = "Create a new lecture")
    @PostMapping("/lecture")
    public ResponseEntity<?> createLecture(@RequestBody LectureRequestDto dto) {
        log.info("Creating new lecture");
        return ResponseEntity.status(HttpStatus.CREATED).body(lectureService.create(dto));
    }

    @Operation(summary = "Create a new quiz")
    @PostMapping("/quiz")
    public ResponseEntity<?> createQuiz(@RequestBody QuizRequestDto dto) {
        log.info("Creating new quiz");
        return ResponseEntity.status(HttpStatus.CREATED).body(quizService.create(dto));
    }

    @Operation(summary = "Create a new Google PDF content")
    @PostMapping("/pdf")
    public ResponseEntity<?> createGooglePdfContent(@RequestBody GooglePdfContentRequestDto dto) {
        log.info("Creating new Google PDF content");
        return ResponseEntity.status(HttpStatus.CREATED).body(googlePdfContentService.create(dto));
    }
}
