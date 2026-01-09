package com.montola.school.admin.service;

import com.montola.school.admin.dto.AdminStatisticsDto;

/**
 * Service for admin-specific operations.
 */
public interface AdminService {

    /**
     * Retrieves aggregated statistics for the dashboard.
     *
     * @return admin statistics DTO
     */
    AdminStatisticsDto getStatistics();
}
