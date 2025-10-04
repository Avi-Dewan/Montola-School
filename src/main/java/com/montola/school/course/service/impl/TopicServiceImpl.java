package com.montola.school.course.service.impl;

import com.montola.school.course.model.Topic;
import com.montola.school.course.repository.TopicRepository;
import com.montola.school.course.service.TopicService;
import com.montola.school.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link TopicService}.
 *
 * Handles persistence and business logic for topics.
 *
 * @author avidewan
 * @date 10/3/25
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    @Override
    @Transactional
    public Topic create(Topic topic) {
        log.info("Creating new topic: {}", topic.getTitle());

        return topicRepository.save(topic);
    }

    @Override
    public List<Topic> getAll() {
        log.debug("Fetching all active topics");

        return topicRepository.findAll()
                .stream()
                .filter(t -> !t.isDeleted())
                .toList();
    }

    @Override
    public Optional<Topic> getById(Long id) {
        log.debug("Fetching topic by ID: {}", id);

        return topicRepository.findById(id)
                .filter(t -> !t.isDeleted());
    }

    @Override
    @Transactional
    public Topic update(Long id, Topic updated) {
        log.info("Updating topic with ID: {}", id);

        return topicRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(updated.getTitle());
                    existing.setDescription(updated.getDescription());

                    return topicRepository.save(existing);
                })
                .orElseThrow(() -> {
                    log.error("Topic not found with ID: {}", id);
                    return new ResourceNotFoundException("topic.notfound");
                });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.warn("Soft deleting topic with ID: {}", id);

        topicRepository.findById(id).ifPresent(entity -> {
            entity.setDeleted(true);
            topicRepository.save(entity);
        });
    }
}
