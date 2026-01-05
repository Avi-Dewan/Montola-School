package com.montola.school.course.service;

import com.montola.school.course.dto.FeaturedChapterResponseDto;
import java.util.List;

/**
 * Service interface for Featured Chapters.
 *
 * @author avidewan
 * @date 1/4/26
 */
public interface FeaturedChapterService {

    void addFeaturedChapter(Long chapterId);

    List<FeaturedChapterResponseDto> getFeaturedChapters();

    void removeFeaturedChapter(Long chapterId);
}
