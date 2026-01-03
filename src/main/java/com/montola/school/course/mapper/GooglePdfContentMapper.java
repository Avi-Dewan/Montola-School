package com.montola.school.course.mapper;

import com.montola.school.course.dto.GooglePdfContentRequestDto;
import com.montola.school.course.dto.GooglePdfContentResponseDto;
import com.montola.school.course.model.contents.file.GooglePdfContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author avidewan
 * @date 11/17/25
 */
@Mapper(componentModel = "spring")
public interface GooglePdfContentMapper {

    GooglePdfContent toEntity(GooglePdfContentRequestDto dto);

    @Mapping(source = "contentItem.topic.id", target = "topicId")
    @Mapping(source = "contentItem.topic.title", target = "topicTitle")
    @Mapping(source = "contentItem.title", target = "title")
    @Mapping(source = "contentItem.orderIndex", target = "orderIndex")
    GooglePdfContentResponseDto toResponseDto(GooglePdfContent entity);

    List<GooglePdfContentResponseDto> toResponseDtoList(List<GooglePdfContent> entities);
}
