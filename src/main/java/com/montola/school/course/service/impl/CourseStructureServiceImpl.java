package com.montola.school.course.service.impl;

import com.montola.school.auth.model.User;
import com.montola.school.auth.repository.UserRepository;
import com.montola.school.auth.service.UserService;
import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.course.dto.structure.*;
import com.montola.school.course.model.Chapter;
import com.montola.school.course.model.ClassEntity;
import com.montola.school.course.model.ContentItem;
import com.montola.school.course.model.Subject;
import com.montola.school.course.model.Topic;
import com.montola.school.course.repository.ChapterRepository;
import com.montola.school.course.repository.ClassRepository;
import com.montola.school.course.repository.ContentItemRepository;
import com.montola.school.course.repository.SubjectRepository;
import com.montola.school.course.repository.TopicRepository;
import com.montola.school.course.service.CourseStructureService;
import com.montola.school.learner.repository.EnrollmentRepository;
import com.montola.school.auth.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.montola.school.course.enums.ChapterStatus.PUBLISHED;

/**
 * @author avidewan
 * @date 12/12/25
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CourseStructureServiceImpl implements CourseStructureService {

    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final ChapterRepository chapterRepository;
    private final TopicRepository topicRepository;
    private final ContentItemRepository contentItemRepository;
    private final EnrollmentRepository enrollmentRepository;

    private final UserService userService;

    @Override
    public ClassStructureResponseDto getClassStructure(Long classId) {
        log.info("Fetching full course structure for class ID: {}", classId);

        // 1. Fetch Class
        ClassEntity classEntity = classRepository.findById(classId)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("class.notfound"));

        // 2. Fetch Subjects
        List<Subject> subjects = subjectRepository.findByClassEntity_Id(classId).stream()
                .filter(s -> !s.isDeleted())
                .sorted(Comparator.comparingInt(Subject::getOrderIndex))
                .collect(Collectors.toList());

        if (subjects.isEmpty()) {
            return buildClassDto(classEntity, Collections.emptyList());
        }

        List<Long> subjectIds = subjects.stream().map(Subject::getId).collect(Collectors.toList());

        User currentUser = userService.getCurrentUser();

        // 3. Fetch Chapters
        List<Chapter> chapters = chapterRepository.findBySubjectIdIn(subjectIds).stream()
                .filter(c -> !c.isDeleted())
                .filter(c -> currentUser.isAdminOrManagerOrTeacher() || c.getStatus() == PUBLISHED)
                .collect(Collectors.toList());

        List<Long> chapterIds = chapters.stream().map(Chapter::getId).collect(Collectors.toList());

        // 4. Fetch Topics
        List<Topic> topics = Collections.emptyList();
        if (!chapterIds.isEmpty()) {
            topics = topicRepository.findByChapterIdIn(chapterIds).stream()
                    .filter(t -> !t.isDeleted())
                    .collect(Collectors.toList());
        }

        // Fetch ContentItems - SKIPPED as only authorized users can see the contents

        // 5. Assemble Tree
        Map<Long, List<Topic>> topicsByChapterId = topics.stream()
                .collect(Collectors.groupingBy(t -> t.getChapter().getId()));

        Map<Long, List<Chapter>> chaptersBySubjectId = chapters.stream()
                .collect(Collectors.groupingBy(c -> c.getSubject().getId()));

        // Build DTOs
        List<SubjectStructureResponseDto> subjectDtos = subjects.stream()
                .map(subject -> {
                    List<Chapter> subjectChapters = chaptersBySubjectId.getOrDefault(subject.getId(), Collections.emptyList());

                    List<ChapterStructureResponseDto> chapterDtos = subjectChapters.stream()
                            .sorted(Comparator.comparingInt(Chapter::getOrderIndex))
                            .map(chapter -> {
                                List<Topic> chapterTopics = topicsByChapterId.getOrDefault(chapter.getId(), Collections.emptyList());

                                List<TopicStructureResponseDto> topicDtos = chapterTopics.stream()
                                        .sorted(Comparator.comparingInt(Topic::getOrderIndex))
                                        .map(topic -> toTopicDto(topic, Collections.emptyList()))
                                        .collect(Collectors.toList());

                                return toChapterDto(chapter, topicDtos);
                            })
                            .collect(Collectors.toList());

                    return toSubjectDto(subject, chapterDtos);
                })
                .collect(Collectors.toList());

        return buildClassDto(classEntity, subjectDtos);
    }

    @Override
    public SubjectStructureResponseDto getSubjectStructure(Long subjectId) {
        log.info("Fetching full structure for subject ID: {}", subjectId);

        User currentUser = userService.getCurrentUser();

        // 1. Fetch Subject
        Subject subject = subjectRepository.findById(subjectId)
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("subject.notfound"));

        // 2. Fetch Chapters and below
        List<Chapter> chapters = chapterRepository.findBySubjectIdIn(Collections.singletonList(subjectId)).stream()
                .filter(c -> !c.isDeleted())
                .filter(c -> currentUser.isAdminOrManagerOrTeacher() || c.getStatus() == PUBLISHED)
                .sorted(Comparator.comparingInt(Chapter::getOrderIndex))
                .toList();

        List<Long> chapterIds = chapters.stream().map(Chapter::getId).collect(Collectors.toList());

        // 3. Fetch Topics
        List<Topic> topics = Collections.emptyList();
        if (!chapterIds.isEmpty()) {
            topics = topicRepository.findByChapterIdIn(chapterIds).stream()
                    .filter(t -> !t.isDeleted())
                    .toList();
        }

        Map<Long, List<Topic>> topicsByChapterId = topics.stream()
                .collect(Collectors.groupingBy(t -> t.getChapter().getId()));

        // Fetch ContentItems - SKIPPED as only authorized users can see the contents

        // 4. Assemble Tree
        List<ChapterStructureResponseDto> chapterDtos = chapters.stream()
                .map(chapter -> {
                    List<Topic> chapterTopics = topicsByChapterId.getOrDefault(chapter.getId(), Collections.emptyList());
                    List<TopicStructureResponseDto> topicDtos = chapterTopics.stream()
                            .sorted(Comparator.comparingInt(Topic::getOrderIndex))
                            .map(topic -> toTopicDto(topic, Collections.emptyList()))
                            .collect(Collectors.toList());

                    return toChapterDto(chapter, topicDtos);
                })
                .collect(Collectors.toList());

        return toSubjectDto(subject, chapterDtos);
    }

    @Override
    public SubjectStructureResponseDto getPublicSubjectStructure(Long subjectId) {
        log.info("Fetching public structure for subject ID: {}", subjectId);

        // 1. Fetch Subject
        Subject subject = subjectRepository.findById(subjectId)
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("subject.notfound"));

        // 2. Fetch PUBLISHED Chapters
        List<Chapter> chapters = chapterRepository.findBySubjectIdIn(Collections.singletonList(subjectId)).stream()
                .filter(c -> !c.isDeleted())
                .filter(c -> c.getStatus() == PUBLISHED)
                .sorted(Comparator.comparingInt(Chapter::getOrderIndex))
                .toList();

        // 3. Assemble Tree (Subject -> Chapter)
        List<ChapterStructureResponseDto> chapterDtos = chapters.stream()
                .map(chapter -> toChapterDto(chapter, Collections.emptyList()))
                .collect(Collectors.toList());

        return toSubjectDto(subject, chapterDtos);
    }

    @Override
    public ChapterStructureResponseDto getChapterStructure(Long chapterId) {
        log.info("Fetching full structure for chapter ID: {}", chapterId);

        User currentUser = userService.getCurrentUser();

        // 1. Fetch Chapter
        Chapter chapter = chapterRepository.findById(chapterId)
                .filter(c -> !c.isDeleted())
                .filter(c -> currentUser.isAdminOrManagerOrTeacher() || c.getStatus() == PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("chapter.notfound"));

        // 2. Fetch Topics and below
        List<Topic> topics = topicRepository.findByChapterIdIn(Collections.singletonList(chapterId)).stream()
                .filter(t -> !t.isDeleted())
                .sorted(Comparator.comparingInt(Topic::getOrderIndex))
                .collect(Collectors.toList());

        List<Long> topicIds = topics.stream().map(Topic::getId).collect(Collectors.toList());

        List<ContentItem> contentItems = Collections.emptyList();
        if (!topicIds.isEmpty()) {
            contentItems = contentItemRepository.findByTopicIdIn(topicIds).stream()
                    .filter(c -> !c.isDeleted())
                    .collect(Collectors.toList());
        }

        // Check Enrollment Status (Access Control). If not enrolled, Hide Content Items
        if (!(currentUser.isAdminOrManagerOrTeacher() ||
                enrollmentRepository.existsByUserIdAndChapterId(currentUser.getId(), chapterId))) {

            contentItems = Collections.emptyList();
            log.info("Chapter {} not enrolled by user {}. Hiding content items.", chapterId, currentUser.getId());
        }

        return buildChapterStructure(chapter, topics, contentItems);
    }

    @Override
    public ChapterStructureResponseDto getPublicChapterStructure(Long chapterId) {
        log.info("Fetching public full structure for chapter ID: {}", chapterId);

        // 1. Fetch Chapter (Only Published)
        Chapter chapter = chapterRepository.findById(chapterId)
                .filter(c -> !c.isDeleted())
                .filter(c -> c.getStatus() == PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("chapter.notfound"));

        // 2. Fetch Topics
        List<Topic> topics = topicRepository.findByChapterIdIn(Collections.singletonList(chapterId)).stream()
                .filter(t -> !t.isDeleted())
                .sorted(Comparator.comparingInt(Topic::getOrderIndex))
                .collect(Collectors.toList());

        List<Long> topicIds = topics.stream().map(Topic::getId).collect(Collectors.toList());

        // 3. Fetch Content Items (No authorization check for structure/titles)
        List<ContentItem> contentItems = Collections.emptyList();
        if (!topicIds.isEmpty()) {
            contentItems = contentItemRepository.findByTopicIdIn(topicIds).stream()
                    .filter(c -> !c.isDeleted())
                    .collect(Collectors.toList());
        }

        return buildChapterStructure(chapter, topics, contentItems);
    }

    private ChapterStructureResponseDto buildChapterStructure(Chapter chapter, List<Topic> topics, List<ContentItem> contentItems) {
        Map<Long, List<ContentItem>> contentByTopicId = contentItems.stream()
                .collect(Collectors.groupingBy(c -> c.getTopic().getId()));

        List<TopicStructureResponseDto> topicDtos = topics.stream()
                .map(topic -> {
                    List<ContentItem> topicContent = contentByTopicId.getOrDefault(topic.getId(), Collections.emptyList());
                    List<ContentItemStructureResponseDto> contentDtos = topicContent.stream()
                            .sorted(Comparator.comparingInt(ContentItem::getOrderIndex))
                            .map(this::toContentDto)
                            .collect(Collectors.toList());

                    return toTopicDto(topic, contentDtos);
                })
                .collect(Collectors.toList());

        return toChapterDto(chapter, topicDtos);
    }

    @Override
    public ClassStructureResponseDto getPublicClassStructure(Long classId) {
        log.info("Fetching public course structure for class ID: {}", classId);

        // 1. Fetch Class
        ClassEntity classEntity = classRepository.findById(classId)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("class.notfound"));

        // 2. Fetch Subjects
        List<Subject> subjects = subjectRepository.findByClassEntity_Id(classId).stream()
                .filter(s -> !s.isDeleted())
                .sorted(Comparator.comparingInt(Subject::getOrderIndex))
                .collect(Collectors.toList());

        if (subjects.isEmpty()) {
            return buildClassDto(classEntity, Collections.emptyList());
        }

        List<Long> subjectIds = subjects.stream().map(Subject::getId).collect(Collectors.toList());

        // 3. Fetch PUBLISHED Chapters (No User Check)
        List<Chapter> chapters = chapterRepository.findBySubjectIdIn(subjectIds).stream()
                .filter(c -> !c.isDeleted())
                .filter(c -> c.getStatus() == PUBLISHED)
                .collect(Collectors.toList());

        Map<Long, List<Chapter>> chaptersBySubjectId = chapters.stream()
                .collect(Collectors.groupingBy(c -> c.getSubject().getId()));

        // 4. Assemble Tree (Class -> Subject -> Chapter)
        List<SubjectStructureResponseDto> subjectDtos = subjects.stream()
                .map(subject -> {
                    List<Chapter> subjectChapters = chaptersBySubjectId.getOrDefault(subject.getId(), Collections.emptyList());

                    List<ChapterStructureResponseDto> chapterDtos = subjectChapters.stream()
                            .sorted(Comparator.comparingInt(Chapter::getOrderIndex))
                            // Pass empty list for topics to keep it lightweight
                            .map(chapter -> toChapterDto(chapter, Collections.emptyList()))
                            .collect(Collectors.toList());

                    return toSubjectDto(subject, chapterDtos);
                })
                .collect(Collectors.toList());

        return buildClassDto(classEntity, subjectDtos);
    }

    private ClassStructureResponseDto buildClassDto(ClassEntity entity, List<SubjectStructureResponseDto> subjects) {
        return ClassStructureResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .subjects(subjects)
                .build();
    }

    private SubjectStructureResponseDto toSubjectDto(Subject entity, List<ChapterStructureResponseDto> chapters) {
        return SubjectStructureResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .orderIndex(entity.getOrderIndex())
                .classId(entity.getClassEntity().getId())
                .className(entity.getClassEntity().getName())
                .chapters(chapters)
                .build();
    }

    private ChapterStructureResponseDto toChapterDto(Chapter entity, List<TopicStructureResponseDto> topics) {
        return ChapterStructureResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .status(entity.getStatus())
                .orderIndex(entity.getOrderIndex())
                .subjectId(entity.getSubject().getId())
                .subjectName(entity.getSubject().getName())
                .classId(entity.getSubject().getClassEntity().getId())
                .className(entity.getSubject().getClassEntity().getName())
                .topics(topics)
                .build();
    }

    private TopicStructureResponseDto toTopicDto(Topic entity, List<ContentItemStructureResponseDto> contentItems) {
        return TopicStructureResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .orderIndex(entity.getOrderIndex())
                .contentItems(contentItems)
                .build();
    }

    private ContentItemStructureResponseDto toContentDto(ContentItem entity) {
        return ContentItemStructureResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .type(entity.getType())
                .orderIndex(entity.getOrderIndex())
                .build();
    }
}
