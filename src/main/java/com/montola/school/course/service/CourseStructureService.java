package com.montola.school.course.service;

import com.montola.school.course.dto.structure.ClassStructureResponseDto;

import com.montola.school.course.dto.structure.ChapterStructureResponseDto;
import com.montola.school.course.dto.structure.SubjectStructureResponseDto;

/**
 * @author avidewan
 * @date 12/12/25
 */
public interface CourseStructureService {

    ClassStructureResponseDto getClassStructure(Long classId);
    SubjectStructureResponseDto getSubjectStructure(Long subjectId);
    ChapterStructureResponseDto getChapterStructure(Long chapterId);
    ChapterStructureResponseDto getPublicChapterStructure(Long chapterId);
    ClassStructureResponseDto getPublicClassStructure(Long classId);
}
