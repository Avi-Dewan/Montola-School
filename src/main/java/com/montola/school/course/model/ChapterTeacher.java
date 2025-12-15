package com.montola.school.course.model;

import com.montola.school.auth.model.User;
import com.montola.school.common.model.Persistent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents the assignment of a teacher to a chapter.
 *
 * @author avidewan
 * @date 12/15/2025
 */
@Entity
@Table(
    name = "chapter_teacher",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"chapter_id", "teacher_id"})},
    indexes = {
        @Index(name = "idx_chapter_teacher_chapter", columnList = "chapter_id"),
        @Index(name = "idx_chapter_teacher_teacher", columnList = "teacher_id")
    }
)
@Getter
@Setter
@SequenceGenerator(name = "chapter_teacher_seq", sequenceName = "chapter_teacher_seq", allocationSize = 1)
public class ChapterTeacher extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chapter_teacher_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", nullable = false)
    private User assignedBy;
}
