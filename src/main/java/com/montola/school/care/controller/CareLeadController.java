package com.montola.school.care.controller;

import com.montola.school.care.dto.CareLeadRequestDto;
import com.montola.school.care.dto.CareLeadResponseDto;
import com.montola.school.care.dto.CareLeadStatusUpdateDto;
import com.montola.school.care.service.CareLeadService;
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
import java.util.Map;

/**
 * Academic Care enquiries: public submission, admin follow-up.
 *
 * @author avidewan
 */
@RestController
@RequestMapping("/api/v1/care")
@RequiredArgsConstructor
@Tag(name = "Academic Care", description = "Enquiry form and lead management")
@Slf4j
public class CareLeadController {

    private final CareLeadService careLeadService;

    @Operation(summary = "Submit an enquiry (public)")
    @PostMapping("/leads")
    public ResponseEntity<Map<String, Object>> submitLead(@Valid @RequestBody CareLeadRequestDto request) {
        Long id = careLeadService.submitLead(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("ok", true, "id", id));
    }

    @Operation(summary = "List enquiries (Admin/Manager)")
    @GetMapping("/admin/leads")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<CareLeadResponseDto>> getLeads() {
        return ResponseEntity.ok(careLeadService.getLeads());
    }

    @Operation(summary = "Update an enquiry's status (Admin/Manager)")
    @PutMapping("/admin/leads/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CareLeadResponseDto> updateStatus(@PathVariable Long id,
                                                            @Valid @RequestBody CareLeadStatusUpdateDto request) {
        return ResponseEntity.ok(careLeadService.updateStatus(id, request.getStatus()));
    }

    @Operation(summary = "Delete an enquiry (Admin/Manager)")
    @DeleteMapping("/admin/leads/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        careLeadService.deleteLead(id);

        return ResponseEntity.noContent().build();
    }
}
