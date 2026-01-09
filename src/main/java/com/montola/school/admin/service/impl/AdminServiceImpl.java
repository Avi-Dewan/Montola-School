package com.montola.school.admin.service.impl;

import com.montola.school.admin.dto.AdminStatisticsDto;
import com.montola.school.admin.service.AdminService;
import com.montola.school.auth.enums.Role;
import com.montola.school.auth.repository.UserRepository;
import com.montola.school.course.enums.ChapterStatus;
import com.montola.school.course.repository.ChapterRepository;
import com.montola.school.course.repository.ClassRepository;
import com.montola.school.course.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link AdminService}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final ChapterRepository chapterRepository;

    @Override
    public AdminStatisticsDto getStatistics() {
        log.info("Calculating admin statistics");

        // User Stats
        long totalUsers = userRepository.countByIsDeletedFalse();
        long activeUsers = userRepository.countByIsActivatedTrueAndIsDeletedFalse();
        long admins = userRepository.countByRolesContainingAndIsDeletedFalse(Role.ADMIN);
        long managers = userRepository.countByRolesContainingAndIsDeletedFalse(Role.MANAGER);
        long teachers = userRepository.countByRolesContainingAndIsDeletedFalse(Role.TEACHER);
        long students = userRepository.countByRolesContainingAndIsDeletedFalse(Role.STUDENT);

        AdminStatisticsDto.UserStats userStats = AdminStatisticsDto.UserStats.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .admins(admins)
                .managers(managers)
                .teachers(teachers)
                .students(students)
                .build();

        // Course Stats
        long totalClasses = classRepository.countByIsDeletedFalse();
        long totalSubjects = subjectRepository.countByIsDeletedFalse();
        long totalChapters = chapterRepository.countByIsDeletedFalse();

        AdminStatisticsDto.CourseStats courseStats = AdminStatisticsDto.CourseStats.builder()
                .totalClasses(totalClasses)
                .totalSubjects(totalSubjects)
                .totalChapters(totalChapters)
                .build();

        // Chapter Stats
        long totalDraft = chapterRepository.countByStatusAndIsDeletedFalse(ChapterStatus.DRAFT);
        long totalPublished = chapterRepository.countByStatusAndIsDeletedFalse(ChapterStatus.PUBLISHED);
        long totalFree = chapterRepository.countByIsFreeAndIsDeletedFalse(true);

        AdminStatisticsDto.ChapterStats chapterStats = AdminStatisticsDto.ChapterStats.builder()
                .totalDraft(totalDraft)
                .totalPublished(totalPublished)
                .totalFree(totalFree)
                .build();

        return AdminStatisticsDto.builder()
                .userStats(userStats)
                .courseStats(courseStats)
                .chapterStats(chapterStats)
                .build();
    }
}
