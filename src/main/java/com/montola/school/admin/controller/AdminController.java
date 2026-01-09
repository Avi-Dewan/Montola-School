package com.montola.school.admin.controller;

import com.montola.school.admin.dto.AdminStatisticsDto;
import com.montola.school.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for admin dashboard operations.
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Dashboard", description = "Endpoints for admin statistics and overview")
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Get admin dashboard statistics")
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<AdminStatisticsDto> getStatistics() {
        log.info("Fetching admin dashboard statistics");
        AdminStatisticsDto statistics = adminService.getStatistics();

        return ResponseEntity.ok(statistics);
    }
}
