package com.montola.school.course.service.impl;

import com.montola.school.course.dto.ChapterRequestDto;
import com.montola.school.course.dto.ChapterResponseDto;
import com.montola.school.course.mapper.ChapterMapper;
import com.montola.school.course.model.Chapter;
import com.montola.school.course.repository.ChapterRepository;
import com.montola.school.course.repository.SubjectRepository;
import com.montola.school.course.repository.TopicRepository;
import com.montola.school.course.service.ChapterAuthorizationService;
import com.montola.school.course.service.ChapterService;
import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.course.model.ChapterTeacher;
import com.montola.school.course.repository.ChapterTeacherRepository;
import com.montola.school.auth.service.UserService;
import com.montola.school.auth.model.User;
import com.montola.school.course.enums.ChapterStatus;
import com.montola.school.course.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ChapterService}.
 * Handles persistence and business logic for chapters.
 *
 * @author avidewan
 * @date 10/3/25
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final ChapterMapper chapterMapper;
    private final TopicService topicService;
    private final ChapterTeacherRepository chapterTeacherRepository;

    private ChapterAuthorizationService chapterAuthorizationService;
    private final UserService userService;

    @Override
    @Transactional
    public ChapterResponseDto create(ChapterRequestDto dto) {
        log.info("Creating new chapter: {}", dto.getTitle());
        Chapter chapter = chapterMapper.toEntity(dto);
        chapter.setSubject(subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("subject.notfound")));

        // Ensure new chapters are created as DRAFT
        if (chapter.getStatus() == null) {
            chapter.setStatus(com.montola.school.course.enums.ChapterStatus.DRAFT);
        }

        // Set the creator from the current security context
        chapter.setCreatedBy(userService.getCurrentUser());

        return chapterMapper.toResponseDto(chapterRepository.save(chapter));
    }

    @Override
    public List<ChapterResponseDto> getAll() {
        log.debug("Fetching all active chapters");

        User user = userService.getCurrentUser();

        return chapterRepository.findAll()
                .stream()
                .filter(c -> !c.isDeleted())
                .filter(c -> {
                    // ADMIN/MANAGER/TEACHER see all
                    if (user.isAdminOrManager() || user.isTeacher()) {
                        return true;
                    }

                    // Students only see PUBLISHED chapters
                    return c.getStatus() == ChapterStatus.PUBLISHED;
                })
                .map(chapterMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ChapterResponseDto> getById(Long id) {
        log.debug("Fetching chapter by ID: {}", id);

        return chapterRepository.findById(id)
                .filter(c -> !c.isDeleted())
                .map(chapterMapper::toResponseDto);
    }

    @Override
    @Transactional
    public ChapterResponseDto update(Long id, ChapterRequestDto dto) {
        log.info("Updating chapter with ID: {}", id);

        return chapterRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(dto.getTitle());
                    existing.setDescription(dto.getDescription());
                    existing.setStatus(dto.getStatus());
                    existing.setOrderIndex(dto.getOrderIndex());
                    existing.setVideoId(dto.getVideoId());
                    existing.setPrice(dto.getPrice());
                    existing.setFree(dto.isFree());

                    return chapterMapper.toResponseDto(chapterRepository.save(existing));
                })
                .orElseThrow(() -> {
                    log.error("Chapter not found with ID: {}", id);
                    return new ResourceNotFoundException("chapter.notfound");
                });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.warn("Soft deleting chapter with ID: {}", id);
        chapterRepository.findById(id).ifPresent(entity -> {
            // Cascade delete topics
            topicRepository.findByChapterId(id).forEach(topic -> {
                topicService.delete(topic.getId());
            });

            entity.setDeleted(true);
            chapterRepository.save(entity);
        });
    }

    @Override
    @Transactional
    public void assignTeacher(Long chapterId, Long teacherId, Long assignedBy) {
        log.info("Assigning teacher {} to chapter {}", teacherId, chapterId);

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("chapter.notfound"));

        User teacher = userService.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("auth.user.notfound"));

        User assigner = userService.findById(assignedBy)
                .orElseThrow(() -> new ResourceNotFoundException("auth.user.notfound"));

        if (!teacher.isTeacher()) {
            log.warn("User {} is not a teacher", teacherId);
            return;
        }

        // Check if already assigned
        if (chapterTeacherRepository.existsByChapterIdAndTeacherId(chapterId, teacherId)) {
            log.warn("Teacher {} already assigned to chapter {}", teacherId, chapterId);
            return;
        }

        ChapterTeacher chapterTeacher = new ChapterTeacher();
        chapterTeacher.setChapter(chapter);
        chapterTeacher.setTeacher(teacher);
        chapterTeacher.setAssignedBy(assigner);
        chapterTeacher.setAssignedAt(java.time.LocalDateTime.now());

        chapterTeacherRepository.save(chapterTeacher);
        log.info("Teacher {} successfully assigned to chapter {}", teacherId, chapterId);
    }

    @Override
    @Transactional
    public void unassignTeacher(Long chapterId, Long teacherId) {
        log.info("Unassigning teacher {} from chapter {}", teacherId, chapterId);
        chapterTeacherRepository.deleteByChapterIdAndTeacherId(chapterId, teacherId);
        log.info("Teacher {} successfully unassigned from chapter {}", teacherId, chapterId);
    }

    @Override
    @Transactional
    public ChapterResponseDto updateStatus(Long id, ChapterStatus status) {
        log.info("Updating status for chapter {}: {}", id, status);

        return chapterRepository.findById(id)
                .map(chapter -> {
                    chapter.setStatus(status);

                    return chapterMapper.toResponseDto(chapterRepository.save(chapter));
                })
                .orElseThrow(() -> new ResourceNotFoundException("chapter.notfound"));
    }

    @Override
    @Transactional
    public ChapterResponseDto toggleFreeStatus(Long id, boolean isFree) {
        log.info("Toggling free status for chapter {}: {}", id, isFree);

        return chapterRepository.findById(id)
                .map(chapter -> {
                    chapter.setFree(isFree);

                    return chapterMapper.toResponseDto(chapterRepository.save(chapter));
                })
                .orElseThrow(() -> new ResourceNotFoundException("chapter.notfound"));
    }

    @Override
    @Transactional
    public void uploadCoverImage(Long id, byte[] imageBytes) {
        log.info("Uploading cover image for chapter {}", id);
        Chapter chapter = chapterRepository.findById(id)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("chapter.notfound"));

        chapter.setCoverImage(imageBytes);

        chapterRepository.save(chapter);

        log.info("Cover image uploaded for chapter {}", id);
    }

    @Override
    public byte[] getCoverImage(Long id) {
        log.debug("Fetching cover image for chapter {}", id);

        Chapter chapter = chapterRepository.findById(id)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("chapter.notfound"));

        return chapter.getCoverImage();
    }

}
