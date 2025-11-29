package com.montola.school.course.repository;

import com.montola.school.course.model.contents.file.GooglePdfContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author avidewan
 * @date 11/29/25
 */
@Repository
public interface GooglePdfContentRepository extends JpaRepository<GooglePdfContent, Long> {
}
