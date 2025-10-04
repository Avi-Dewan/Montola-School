package com.montola.school.course.service.impl;

import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.course.model.Subject;
import com.montola.school.course.repository.SubjectRepository;
import com.montola.school.course.service.SubjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link SubjectService}.
 *
 * Handles persistence and business logic for subjects.
 *
 * @author avidewan
 * @date 10/3/25
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    @Override
    @Transactional
    public Subject create(Subject subject) {
        log.info("Creating new subject: {}", subject.getName());

        return subjectRepository.save(subject);
    }

    @Override
    public List<Subject> getAll() {
        log.debug("Fetching all active subjects");

        return subjectRepository.findAll()
                .stream()
                .filter(s -> !s.isDeleted())
                .toList();
    }

    @Override
    public Optional<Subject> getById(Long id) {
        log.debug("Fetching subject by ID: {}", id);

        return subjectRepository.findById(id)
                .filter(s -> !s.isDeleted());
    }

    @Override
    @Transactional
    public Subject update(Long id, Subject updated) {
        log.info("Updating subject with ID: {}", id);

        return subjectRepository.findById(id)
                .map(existing -> {
                    existing.setName(updated.getName());
                    existing.setDescription(updated.getDescription());
                    return subjectRepository.save(existing);
                })
                .orElseThrow(() -> {
                    log.error("Subject not found with ID: {}", id);

                    return new ResourceNotFoundException("subject.notfound");
                });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.warn("Soft deleting subject with ID: {}", id);

        subjectRepository.findById(id).ifPresent(entity -> {
            entity.setDeleted(true);
            subjectRepository.save(entity);
        });
    }
}
