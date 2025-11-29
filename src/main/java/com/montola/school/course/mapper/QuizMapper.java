package com.montola.school.course.mapper;

import com.montola.school.course.dto.QuizRequestDto;
import com.montola.school.course.dto.QuizResponseDto;
import com.montola.school.course.model.contents.Quiz;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author avidewan
 * @date 11/17/25
 */
@Mapper(componentModel = "spring")
public interface QuizMapper {

    Quiz toEntity(QuizRequestDto dto);

    @Mapping(source = "contentItem.topic.id", target = "topicId")
    @Mapping(source = "contentItem.topic.title", target = "topicTitle")
    @Mapping(source = "contentItem.title", target = "title")
    @Mapping(source = "contentItem.orderIndex", target = "orderIndex")
    QuizResponseDto toResponseDto(Quiz entity);

    List<QuizResponseDto> toResponseDtoList(List<Quiz> entities);
}
