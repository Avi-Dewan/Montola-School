package com.montola.school.course.mapper;

import com.montola.school.course.dto.SubjectRequestDto;
import com.montola.school.course.dto.SubjectResponseDto;
import com.montola.school.course.model.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author avidewan
 * @date 12/1/25
 */
@Mapper(componentModel = "spring")
public interface SubjectMapper {

    Subject toEntity(SubjectRequestDto dto);

    @Mapping(source = "classEntity.id", target = "classId")
    @Mapping(source = "classEntity.name", target = "className")
    SubjectResponseDto toResponseDto(Subject entity);
}
