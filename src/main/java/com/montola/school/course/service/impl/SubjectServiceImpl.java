package com.montola.school.course.service.impl;

import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.course.dto.SubjectRequestDto;
import com.montola.school.course.dto.SubjectResponseDto;
import com.montola.school.course.mapper.SubjectMapper;
import com.montola.school.course.model.Subject;
import com.montola.school.course.repository.ClassRepository;
import com.montola.school.course.repository.SubjectRepository;
import com.montola.school.course.service.SubjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final ClassRepository classRepository;
    private final SubjectMapper subjectMapper;

    @Override
    @Transactional
    public SubjectResponseDto create(SubjectRequestDto dto) {
        log.info("Creating new subject: {}", dto.getName());
        Subject subject = subjectMapper.toEntity(dto);
        subject.setClassEntity(classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("class.notfound")));
        return subjectMapper.toResponseDto(subjectRepository.save(subject));
    }

    @Override
    public List<SubjectResponseDto> getAll() {
        log.debug("Fetching all active subjects");
        return subjectRepository.findAll()
                .stream()
                .filter(s -> !s.isDeleted())
                .map(subjectMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SubjectResponseDto> getById(Long id) {
        log.debug("Fetching subject by ID: {}", id);
        return subjectRepository.findById(id)
                .filter(s -> !s.isDeleted())
                .map(subjectMapper::toResponseDto);
    }

    @Override
    @Transactional
    public SubjectResponseDto update(Long id, SubjectRequestDto dto) {
        log.info("Updating subject with ID: {}", id);
        return subjectRepository.findById(id)
                .map(existing -> {
                    existing.setName(dto.getName());
                    existing.setDescription(dto.getDescription());
                    existing.setOrderIndex(dto.getOrderIndex());
                    return subjectMapper.toResponseDto(subjectRepository.save(existing));
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
