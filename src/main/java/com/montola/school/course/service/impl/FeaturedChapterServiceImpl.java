package com.montola.school.course.service.impl;

import com.montola.school.common.exception.ResourceAlreadyExistsException;
import com.montola.school.common.exception.ResourceNotFoundException;
import com.montola.school.course.dto.FeaturedChapterResponseDto;
import com.montola.school.course.enums.ChapterStatus;
import com.montola.school.course.model.Chapter;
import com.montola.school.course.model.FeaturedChapter;
import com.montola.school.course.repository.ChapterRepository;
import com.montola.school.course.repository.FeaturedChapterRepository;
import com.montola.school.course.service.FeaturedChapterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of FeaturedChapterService.
 *
 * @author avidewan
 * @date 1/4/26
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FeaturedChapterServiceImpl implements FeaturedChapterService {

    private final FeaturedChapterRepository featuredChapterRepository;
    private final ChapterRepository chapterRepository;

    @Override
    @Transactional
    public void addFeaturedChapter(Long chapterId) {
        log.info("Adding chapter {} to featured list", chapterId);

        if (featuredChapterRepository.existsByChapterId(chapterId)) {
            log.warn("Chapter {} is already featured", chapterId);

            throw new ResourceAlreadyExistsException("chapter.already.featured");
        }

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("chapter.notfound"));

        if (chapter.getStatus() != ChapterStatus.PUBLISHED) {
            log.warn("Cannot feature unpublished chapter {}", chapterId);
            throw new IllegalArgumentException("Only PUBLISHED chapters can be featured.");
        }

        FeaturedChapter featuredChapter = new FeaturedChapter();
        featuredChapter.setChapter(chapter);

        featuredChapterRepository.save(featuredChapter);
        log.info("Chapter {} successfully featured", chapterId);
    }

    @Override
    public List<FeaturedChapterResponseDto> getFeaturedChapters() {
        log.debug("Fetching all featured chapters");
        return featuredChapterRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeFeaturedChapter(Long chapterId) {
        log.info("Removing chapter {} from featured list", chapterId);
        
        FeaturedChapter featured = featuredChapterRepository.findByChapterId(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("featured.chapter.notfound"));
                
        featuredChapterRepository.delete(featured);

        log.info("Chapter {} removed from featured list", chapterId);
    }

    private FeaturedChapterResponseDto mapToDto(FeaturedChapter featured) {
        Chapter chapter = featured.getChapter();

        return FeaturedChapterResponseDto.builder()
                .id(featured.getId())
                .chapterId(chapter.getId())
                .title(chapter.getTitle())
                .description(chapter.getDescription())
                .subjectName(chapter.getSubject().getName())
                .className(chapter.getSubject().getClassEntity().getName())
                .price(chapter.getPrice())
                .isFree(chapter.isFree())
                .featuredAt(featured.getCreatedAt())
                .build();
    }
}
