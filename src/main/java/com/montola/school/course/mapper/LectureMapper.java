package com.montola.school.course.mapper;

import com.montola.school.course.dto.LectureRequestDto;
import com.montola.school.course.dto.LectureResponseDto;
import com.montola.school.course.model.Lecture;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author avidewan
 * @date 10/11/25
 */
@Mapper(componentModel = "spring")
public interface LectureMapper {

    Lecture toEntity(LectureRequestDto dto);

    LectureResponseDto toResponseDto(Lecture entity);

    List<LectureResponseDto> toResponseDtoList(List<Lecture> entities);
}

