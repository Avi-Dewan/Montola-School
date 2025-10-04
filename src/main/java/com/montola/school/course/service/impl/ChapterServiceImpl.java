package com.montola.school.course.service.impl;

import com.montola.school.course.model.Chapter;
import com.montola.school.course.repository.ChapterRepository;
import com.montola.school.course.service.ChapterService;
import com.montola.school.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    @Override
    @Transactional
    public Chapter create(Chapter chapter) {
        log.info("Creating new chapter: {}", chapter.getTitle());

        return chapterRepository.save(chapter);
    }

    @Override
    public List<Chapter> getAll() {
        log.debug("Fetching all active chapters");

        return chapterRepository.findAll()
                .stream()
                .filter(c -> !c.isDeleted())
                .toList();
    }

    @Override
    public Optional<Chapter> getById(Long id) {
        log.debug("Fetching chapter by ID: {}", id);

        return chapterRepository.findById(id)
                .filter(c -> !c.isDeleted());
    }

    @Override
    @Transactional
    public Chapter update(Long id, Chapter updated) {
        log.info("Updating chapter with ID: {}", id);

        return chapterRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(updated.getTitle());
                    existing.setDescription(updated.getDescription());
                    existing.setStatus(updated.getStatus());

                    return chapterRepository.save(existing);
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
            entity.setDeleted(true);
            chapterRepository.save(entity);
        });
    }
}
