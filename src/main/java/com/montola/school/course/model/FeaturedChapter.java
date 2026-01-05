package com.montola.school.course.model;

import com.montola.school.common.model.Persistent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity for Featured Chapters.
 *
 * @author avidewan
 * @date 1/4/26
 */
@Entity
@Table(name = "featured_chapters")
@Getter
@Setter
@SequenceGenerator(name = "featured_chapters_seq", sequenceName = "featured_chapters_seq", allocationSize = 1)
public class FeaturedChapter extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "featured_chapters_seq")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;
}
