package com.montola.school.course.mapper;

import com.montola.school.auth.model.User;
import com.montola.school.course.dto.ChapterRequestDto;
import com.montola.school.course.dto.ChapterResponseDto;
import com.montola.school.course.model.Chapter;
import com.montola.school.course.model.ChapterTeacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author avidewan
 * @date 12/1/25
 */
@Mapper(componentModel = "spring")
public interface ChapterMapper {

    @Mapping(target = "subject", ignore = true) // Subject is set in the service layer
    @Mapping(target = "createdBy", ignore = true) // CreatedBy is set in the service layer
    @Mapping(target = "coverImage", ignore = true) // Cover image is handled separately
    @Mapping(target = "topics", ignore = true) // Topics are handled separately
    @Mapping(target = "teachers", ignore = true) // Teachers are handled separately
    Chapter toEntity(ChapterRequestDto dto);

    @Mapping(target = "subjectId", source = "subject.id")
    @Mapping(target = "subjectName", source = "subject.name")
    @Mapping(target = "teachers", source = "teachers", qualifiedByName = "mapTeachers")
    ChapterResponseDto toResponseDto(Chapter entity);

    @Named("mapTeachers")
    default List<ChapterResponseDto.TeacherDto> mapTeachers(List<ChapterTeacher> chapterTeachers) {
        if (chapterTeachers == null) {
            return Collections.emptyList();
        }
        
        return chapterTeachers.stream()
                .map(ChapterTeacher::getTeacher)
                .map(this::toTeacherDto)
                .collect(Collectors.toList());
    }

    default ChapterResponseDto.TeacherDto toTeacherDto(User user) {
        if (user == null) {
            return null;
        }

        ChapterResponseDto.TeacherDto dto = new ChapterResponseDto.TeacherDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());

        return dto;
    }
}