package com.montola.school.care.service.impl;

import com.montola.school.care.dto.CareLeadRequestDto;
import com.montola.school.care.dto.CareLeadResponseDto;
import com.montola.school.care.enums.CareLeadStatus;
import com.montola.school.care.model.CareLead;
import com.montola.school.care.repository.CareLeadRepository;
import com.montola.school.care.service.CareLeadService;
import com.montola.school.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author avidewan
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CareLeadServiceImpl implements CareLeadService {

    private final CareLeadRepository careLeadRepository;

    @Override
    @Transactional
    public Long submitLead(CareLeadRequestDto request) {
        CareLead lead = new CareLead();
        lead.setName(request.getName().trim());
        lead.setPhone(request.getPhone().trim());
        lead.setLevel(request.getLevel());
        lead.setArea(request.getArea());
        lead.setMessage(request.getMessage());
        lead.setStatus(CareLeadStatus.NEW);

        CareLead saved = careLeadRepository.save(lead);
        log.info("Academic Care lead received (id={})", saved.getId());

        return saved.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CareLeadResponseDto> getLeads() {
        return careLeadRepository.findByIsDeletedFalseOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CareLeadResponseDto updateStatus(Long leadId, CareLeadStatus status) {
        CareLead lead = findLead(leadId);
        lead.setStatus(status);

        return toResponse(careLeadRepository.save(lead));
    }

    @Override
    @Transactional
    public void deleteLead(Long leadId) {
        CareLead lead = findLead(leadId);
        lead.setDeleted(true);
        careLeadRepository.save(lead);

        log.info("Soft-deleted Academic Care lead {}", leadId);
    }

    private CareLead findLead(Long leadId) {
        return careLeadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("care.lead.notfound"));
    }

    private CareLeadResponseDto toResponse(CareLead lead) {
        return CareLeadResponseDto.builder()
                .id(lead.getId())
                .name(lead.getName())
                .phone(lead.getPhone())
                .level(lead.getLevel())
                .area(lead.getArea())
                .message(lead.getMessage())
                .status(lead.getStatus())
                .createdAt(lead.getCreatedAt())
                .build();
    }
}
