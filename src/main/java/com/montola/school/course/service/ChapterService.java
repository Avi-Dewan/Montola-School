package com.montola.school.course.service;

import com.montola.school.course.dto.ChapterRequestDto;
import com.montola.school.course.dto.ChapterResponseDto;
import com.montola.school.course.enums.ChapterStatus;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing chapters within subjects.
 *
 * Provides methods to create, update, fetch, and soft-delete chapters.
 *
 * @author avidewan
 * @date 10/3/25
 */
public interface ChapterService {

    /**
     * Creates a new chapter.
     *
     * @param dto the chapter entity to be created
     * @return the saved chapter entity
     */
    ChapterResponseDto create(ChapterRequestDto dto);

    /**
     * Retrieves all active (non-deleted) chapters.
     *
     * @return list of chapters
     */
    List<ChapterResponseDto> getAll();

    /**
     * Retrieves a chapter by its ID if not deleted.
     *
     * @param id the chapter ID
     * @return optional chapter entity
     */
    Optional<ChapterResponseDto> getById(Long id);

    /**
     * Updates an existing chapter.
     *
     * @param id the chapter ID
     * @param dto the updated chapter details
     * @return the updated chapter entity
     */
    ChapterResponseDto update(Long id, ChapterRequestDto dto);

    /**
     * Soft deletes a chapter by marking it as deleted.
     *
     * @param id the chapter ID
     */
    void delete(Long id);

    /**
     * Assign a teacher to a chapter.
     *
     * @param chapterId the chapter ID
     * @param teacherId the teacher user ID
     * @param assignedBy the user ID who is assigning
     */
    void assignTeacher(Long chapterId, Long teacherId, Long assignedBy);

    /**
     * Unassign a teacher from a chapter.
     *
     * @param chapterId the chapter ID
     * @param teacherId the teacher user ID
     */
    void unassignTeacher(Long chapterId, Long teacherId);

    /**
     * Updates the status of a chapter.
     *
     * @param id the chapter ID
     * @param status the new status
     * @return the updated chapter entity
     */
    ChapterResponseDto updateStatus(Long id, ChapterStatus status);

    /**
     * Toggles the free status of a chapter.
     *
     * @param id the chapter ID
     * @param isFree the new free status
     * @return the updated chapter entity
     */
    ChapterResponseDto toggleFreeStatus(Long id, boolean isFree);


    void uploadCoverImage(Long id, byte[] imageBytes);

    byte[] getCoverImage(Long id);

    /**
     * Retrieves chapters by their status.
     *
     * @param status the status to filter by
     * @return list of chapters with the given status
     */
    List<ChapterResponseDto> getChaptersByStatus(ChapterStatus status);

    /**
     * Retrieves a single public chapter by ID.
     * Throws exception if not found or not published.
     *
     * @param id the chapter ID
     * @return the chapter entity
     */
    ChapterResponseDto getPublicChapter(Long id);

    /**
     * Retrieves all free and published chapters.
     *
     * @return list of free published chapters
     */
    List<ChapterResponseDto> getFreeChapters();
}
