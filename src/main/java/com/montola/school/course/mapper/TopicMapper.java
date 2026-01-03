package com.montola.school.course.mapper;

import com.montola.school.course.dto.TopicRequestDto;
import com.montola.school.course.dto.TopicResponseDto;
import com.montola.school.course.model.Topic;
import org.mapstruct.Mapper;

/**
 * @author avidewan
 * @date 12/1/25
 */
@Mapper(componentModel = "spring")
public interface TopicMapper {
    Topic toEntity(TopicRequestDto dto);
    TopicResponseDto toResponseDto(Topic entity);
}
