package com.montola.school.learner.model;

import com.montola.school.auth.model.User;
import com.montola.school.common.model.Persistent;
import com.montola.school.course.model.ContentItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Tracks a student's progress on a specific content item.
 *
 * @author avidewan
 * @date 12/13/2025
 */
@Entity
@Table(
    name = "content_progress",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "content_item_id"})},
    indexes = {@Index(name = "idx_progress_user_content", columnList = "user_id, content_item_id")}
)
@Getter
@Setter
@SequenceGenerator(name = "content_progress_seq_gen", sequenceName = "content_progress_seq", allocationSize = 1)
public class ContentProgress extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "content_progress_seq_gen")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_item_id", nullable = false)
    private ContentItem contentItem;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted = false;

    @Column(name = "quiz_score")
    private Double quizScore;

    @Column(name = "last_accessed")
    private LocalDateTime lastAccessed;
}
