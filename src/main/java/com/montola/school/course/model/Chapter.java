package com.montola.school.course.model;

import com.montola.school.auth.model.User;
import com.montola.school.common.model.Persistent;
import com.montola.school.course.enums.ChapterStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author avidewan
 * @date 9/29/25
 */
@Entity
@Table(name = "chapters")
@Getter
@Setter
@SequenceGenerator(name = "chapters_seq", sequenceName = "chapters_seq", allocationSize = 1)
public class Chapter extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chapters_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChapterStatus status = ChapterStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "chapter", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Topic> topics;
}
