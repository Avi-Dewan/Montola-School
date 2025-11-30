package com.montola.school.course.mapper;

import com.montola.school.course.dto.SubjectRequestDto;
import com.montola.school.course.dto.SubjectResponseDto;
import com.montola.school.course.model.Subject;
import org.mapstruct.Mapper;

/**
 * @author avidewan
 * @date 12/1/25
 */
@Mapper(componentModel = "spring")
public interface SubjectMapper {
    Subject toEntity(SubjectRequestDto dto);
    SubjectResponseDto toResponseDto(Subject entity);
}
