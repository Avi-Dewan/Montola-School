package com.montola.school.course.service.impl;

import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.course.model.ClassEntity;
import com.montola.school.course.repository.ClassRepository;
import com.montola.school.course.service.ClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link ClassService}.
 *
 * Handles persistence and business logic for classes.
 *
 * @author avidewan
 * @date 10/3/25
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;

    @Override
    @Transactional
    public ClassEntity create(ClassEntity classEntity) {
        log.info("Creating new class: {}", classEntity.getName());

        return classRepository.save(classEntity);
    }

    @Override
    public List<ClassEntity> getAll() {
        log.debug("Fetching all active classes");

        return classRepository.findAll()
                .stream()
                .filter(c -> !c.isDeleted())
                .toList();
    }

    @Override
    public Optional<ClassEntity> getById(Long id) {
        log.debug("Fetching class by ID: {}", id);

        return classRepository.findById(id)
                .filter(c -> !c.isDeleted());
    }

    @Override
    @Transactional
    public ClassEntity update(Long id, ClassEntity updated) {
        log.info("Updating class with ID: {}", id);

        return classRepository.findById(id)
                .map(existing -> {
                    existing.setName(updated.getName());
                    existing.setDescription(updated.getDescription());

                    return classRepository.save(existing);
                })
                .orElseThrow(() -> {
                    log.error("Class not found with ID: {}", id);

                    return new ResourceNotFoundException("class.notfound");
                });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.warn("Soft deleting class with ID: {}", id);

        classRepository.findById(id).ifPresent(entity -> {
            entity.setDeleted(true);
            classRepository.save(entity);
        });
    }
}
