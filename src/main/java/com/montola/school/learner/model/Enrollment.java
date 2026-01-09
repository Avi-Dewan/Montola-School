package com.montola.school.learner.model;

import com.montola.school.auth.model.User;
import com.montola.school.common.model.Persistent;
import com.montola.school.course.model.Chapter;
import com.montola.school.course.model.ContentItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents a verified enrollment in a chapter.
 *
 * @author avidewan
 * @date 12/14/2025
 */
@Entity
@Table(
    name = "enrollments",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "chapter_id"})},
    indexes = {@Index(name = "idx_enrollment_user_chapter", columnList = "user_id, chapter_id")}
)
@Getter
@Setter
@SequenceGenerator(name = "enrollments_seq_gen", sequenceName = "enrollments_seq", allocationSize = 1)
public class Enrollment extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "enrollments_seq_gen")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(name = "enrolled_at", nullable = false)
    private LocalDateTime enrolledAt;

    @Column(name = "progress_percentage")
    private Double progressPercentage = 0.0;

    @Column(name = "is_completed")
    private boolean isCompleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_completed_content_id")
    private ContentItem lastCompletedContent;
}
