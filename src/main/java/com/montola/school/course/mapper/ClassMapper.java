package com.montola.school.course.mapper;

import com.montola.school.course.dto.ClassRequestDto;
import com.montola.school.course.dto.ClassResponseDto;
import com.montola.school.course.model.ClassEntity;
import org.mapstruct.Mapper;

/**
 * @author avidewan
 * @date 12/1/25
 */
@Mapper(componentModel = "spring")
public interface ClassMapper {
    ClassEntity toEntity(ClassRequestDto dto);
    ClassResponseDto toResponseDto(ClassEntity entity);
}
