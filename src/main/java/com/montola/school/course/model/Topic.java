package com.montola.school.course.model;

import com.montola.school.common.model.Persistent;
import com.montola.school.course.model.contents.Lecture;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author avidewan
 * @date 9/29/25
 */
@Entity
@Table(name = "topics")
@Getter
@Setter
@SequenceGenerator(name = "topics_seq", sequenceName = "topics_seq", allocationSize = 1)
public class Topic extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "topics_seq")
    private Long id;

    @Column(nullable = false, name = "order_index")
    private int orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lecture> lectures;
}

