package com.montola.school.course.model;

import com.montola.school.common.model.Persistent;

import com.montola.school.course.enums.ContentItemType;
import com.montola.school.course.model.contents.Lecture;
import com.montola.school.course.model.contents.Quiz;
import com.montola.school.course.model.contents.file.GooglePdfContent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author avidewan
 * @date 10/21/25
 */
@Entity
@Table(name = "content_items")
@Getter
@Setter
@SequenceGenerator(name = "content_items_seq", sequenceName = "content_items_seq", allocationSize = 1)
public class ContentItem extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "content_items_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ContentItemType type;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @OneToOne(mappedBy = "contentItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Lecture lecture;

    @OneToOne(mappedBy = "contentItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Quiz quiz;

    @OneToOne(mappedBy = "contentItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private GooglePdfContent googlePdfContent;
}
