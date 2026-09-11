package com.montola.school.notice.controller;

import com.montola.school.notice.dto.NoticeRequestDto;
import com.montola.school.notice.dto.NoticeResponseDto;
import com.montola.school.notice.service.NoticeService;
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
 * Homepage notices: public read, admin management.
 *
 * @author avidewan
 */
@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
@Tag(name = "Notices", description = "Homepage notices")
@Slf4j
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "Active notices (public)")
    @GetMapping
    public ResponseEntity<List<NoticeResponseDto>> getActiveNotices() {
        return ResponseEntity.ok(noticeService.getActiveNotices());
    }

    @Operation(summary = "All notices, including hidden (Admin/Manager)")
    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<NoticeResponseDto>> getAllNotices() {
        return ResponseEntity.ok(noticeService.getAllNotices());
    }

    @Operation(summary = "Create a notice (Admin/Manager)")
    @PostMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<NoticeResponseDto> createNotice(@Valid @RequestBody NoticeRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noticeService.createNotice(request));
    }

    @Operation(summary = "Update a notice (Admin/Manager)")
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<NoticeResponseDto> updateNotice(@PathVariable Long id,
                                                          @Valid @RequestBody NoticeRequestDto request) {
        return ResponseEntity.ok(noticeService.updateNotice(id, request));
    }

    @Operation(summary = "Delete a notice (Admin/Manager)")
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);

        return ResponseEntity.noContent().build();
    }
}
