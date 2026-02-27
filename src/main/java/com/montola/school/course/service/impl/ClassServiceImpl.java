package com.montola.school.course.service.impl;

import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.course.dto.ClassRequestDto;
import com.montola.school.course.dto.ClassResponseDto;
import com.montola.school.course.mapper.ClassMapper;
import com.montola.school.course.model.ClassEntity;
import com.montola.school.course.repository.ClassRepository;
import com.montola.school.course.service.ClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final com.montola.school.course.repository.SubjectRepository subjectRepository;
    private final ClassMapper classMapper;
    private final com.montola.school.course.service.SubjectService subjectService;

    @Override
    @Transactional
    public ClassResponseDto create(ClassRequestDto dto) {
        ClassEntity classEntity = classMapper.toEntity(dto);

        return classMapper.toResponseDto(classRepository.save(classEntity));
    }

    @Override
    public List<ClassResponseDto> getAll() {
        return classRepository.findAll()
                .stream()
                .filter(c -> !c.isDeleted())
                .map(classMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ClassResponseDto> getById(Long id) {
        return classRepository.findById(id)
                .filter(c -> !c.isDeleted())
                .map(classMapper::toResponseDto);
    }

    @Override
    @Transactional
    public ClassResponseDto update(Long id, ClassRequestDto dto) {
        return classRepository.findById(id)
                .map(existing -> {
                    existing.setName(dto.getName());
                    existing.setDescription(dto.getDescription());
                    return classMapper.toResponseDto(classRepository.save(existing));
                })
                .orElseThrow(() -> {
                    log.error("Class not found with ID: {}", id);
                    return new ResourceNotFoundException("class.notfound");
                });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ClassEntity entity = classRepository.findById(id).orElseThrow(() -> {
            log.error("Class not found with ID: {}", id);
            return new ResourceNotFoundException("class.notfound");
        });

        // Cascade delete subjects
        subjectRepository.findByClassEntity_Id(id).forEach(subject -> {
            subjectService.delete(subject.getId());
        });

        entity.setDeleted(true);
        classRepository.save(entity);
    }
}
