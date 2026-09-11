package com.montola.school.course.model;

import com.montola.school.common.model.Persistent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author avidewan
 * @date 9/29/25
 */
@Entity
@Table(name = "classes")
@Getter
@Setter
@SequenceGenerator(name = "classes_seq", sequenceName = "classes_seq", allocationSize = 1)
public class ClassEntity extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "classes_seq")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private Level level;

    @OneToMany(mappedBy = "classEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subject> subjects;
}