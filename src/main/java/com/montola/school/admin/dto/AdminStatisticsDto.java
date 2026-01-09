package com.montola.school.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for aggregated admin statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatisticsDto {

    private UserStats userStats;
    private CourseStats courseStats;
    private ChapterStats chapterStats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStats {
        private long totalUsers;
        private long activeUsers;
        private long admins;
        private long managers;
        private long teachers;
        private long students;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseStats {
        private long totalClasses;
        private long totalSubjects;
        private long totalChapters;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChapterStats {
        private long totalDraft;
        private long totalPublished;
        private long totalFree;
    }
}
