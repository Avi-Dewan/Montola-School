package com.montola.school.course.service.impl;

import com.montola.school.course.model.Lecture;
import com.montola.school.course.repository.LectureRepository;
import com.montola.school.course.service.LectureService;
import com.montola.school.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link LectureService}.
 *
 * Handles persistence and business logic for lectures.
 *
 * @author avidewan
 * @date 10/3/25
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class LectureServiceImpl implements LectureService {

    private final LectureRepository lectureRepository;

    @Override
    @Transactional
    public Lecture create(Lecture lecture) {
        log.info("Creating new lecture: {}", lecture.getTitle());
        return lectureRepository.save(lecture);
    }

    @Override
    public List<Lecture> getAll() {
        log.debug("Fetching all active lectures");

        return lectureRepository.findAll()
                .stream()
                .filter(l -> !l.isDeleted())
                .toList();
    }

    @Override
    public Optional<Lecture> getById(Long id) {
        log.debug("Fetching lecture by ID: {}", id);

        return lectureRepository.findById(id)
                .filter(l -> !l.isDeleted());
    }

    @Override
    @Transactional
    public Lecture update(Long id, Lecture updated) {
        log.info("Updating lecture with ID: {}", id);

        return lectureRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(updated.getTitle());
                    existing.setContent(updated.getContent());
                    existing.setVideoId(updated.getVideoId());

                    return lectureRepository.save(existing);
                })
                .orElseThrow(() -> {
                    log.error("Lecture not found with ID: {}", id);
                    return new ResourceNotFoundException("lecture.notfound");
                });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.warn("Soft deleting lecture with ID: {}", id);

        lectureRepository.findById(id).ifPresent(entity -> {
            entity.setDeleted(true);
            lectureRepository.save(entity);
        });
    }
}
