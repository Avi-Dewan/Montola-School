package com.montola.school.course.controller;

import com.montola.school.course.dto.TopicRequestDto;
import com.montola.school.course.dto.TopicResponseDto;
import com.montola.school.course.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author avidewan
 * @date 11/30/25
 */
@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
@Tag(name = "Topic Management", description = "Endpoints to manage topics")
@Slf4j
public class TopicController {

    private final TopicService topicService;

    @Operation(summary = "Create a new topic")
    @PostMapping
    public ResponseEntity<TopicResponseDto> createTopic(@Valid @RequestBody TopicRequestDto dto) {
        log.info("Creating new topic: {}", dto.getTitle());
        TopicResponseDto createdTopic = topicService.create(dto);
        log.info("Topic created with id: {}", createdTopic.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTopic);
    }

    @Operation(summary = "Get all topics")
    @GetMapping
    public ResponseEntity<List<TopicResponseDto>> getAllTopics() {
        log.info("Fetching all topics");
        List<TopicResponseDto> topics = topicService.getAll();
        log.debug("Total topics found: {}", topics.size());

        return ResponseEntity.ok(topics);
    }

    @Operation(summary = "Get topic by id")
    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseDto> getTopicById(@PathVariable Long id) {
        log.info("Fetching topic by id: {}", id);
        return topicService.getById(id)
                .map(topicDto -> {
                    log.debug("Topic found with id {}", id);
                    return ResponseEntity.ok(topicDto);
                })
                .orElseGet(() -> {
                    log.warn("Topic not found with id {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @Operation(summary = "Update a topic")
    @PutMapping("/{id}")
    public ResponseEntity<TopicResponseDto> updateTopic(@PathVariable Long id, @Valid @RequestBody TopicRequestDto dto) {
        log.info("Updating topic with id: {}", id);
        TopicResponseDto updatedTopic = topicService.update(id, dto);
        log.info("Topic updated with id: {}", updatedTopic.getId());

        return ResponseEntity.ok(updatedTopic);
    }

    @Operation(summary = "Delete a topic")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        log.info("Deleting topic with id: {}", id);
        topicService.delete(id);
        log.info("Topic deleted with id: {}", id);

        return ResponseEntity.noContent().build();
    }
}
