package com.montola.school.care.service;

import com.montola.school.care.dto.CareLeadRequestDto;
import com.montola.school.care.dto.CareLeadResponseDto;
import com.montola.school.care.enums.CareLeadStatus;

import java.util.List;

/**
 * Academic Care enquiry capture and follow-up.
 *
 * @author avidewan
 */
public interface CareLeadService {

    Long submitLead(CareLeadRequestDto request);

    List<CareLeadResponseDto> getLeads();

    CareLeadResponseDto updateStatus(Long leadId, CareLeadStatus status);

    void deleteLead(Long leadId);
}
