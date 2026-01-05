package com.montola.school.course.mapper;

import com.montola.school.course.dto.ChapterRequestDto;
import com.montola.school.course.dto.ChapterResponseDto;
import com.montola.school.course.model.Chapter;
import org.mapstruct.Mapper;

/**
 * @author avidewan
 * @date 12/1/25
 */
@Mapper(componentModel = "spring")
public interface ChapterMapper {

    Chapter toEntity(ChapterRequestDto dto);

    @org.mapstruct.Mapping(target = "author", source = "createdBy")
    ChapterResponseDto toResponseDto(Chapter entity);
}
