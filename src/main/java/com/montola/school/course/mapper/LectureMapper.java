package com.montola.school.course.mapper;

import com.montola.school.course.dto.LectureRequestDto;
import com.montola.school.course.dto.LectureResponseDto;
import com.montola.school.course.model.ContentItem;
import com.montola.school.course.model.contents.Lecture;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author avidewan
 * @date 10/11/25
 */
@Mapper(componentModel = "spring")
public interface LectureMapper {

    Lecture toEntity(LectureRequestDto dto);

    @Mapping(source = "contentItem.topic.id", target = "topicId")
    @Mapping(source = "contentItem.topic.title", target = "topicTitle")
    @Mapping(source = "contentItem.title", target = "title")
    @Mapping(source = "contentItem.orderIndex", target = "orderIndex")
    LectureResponseDto toResponseDto(Lecture entity);

    List<LectureResponseDto> toResponseDtoList(List<Lecture> entities);
}
