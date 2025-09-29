package com.montola.school.course.model;

import com.montola.school.common.model.Persistent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author avidewan
 * @date 9/29/25
 */
@Entity
@Table(name = "lectures")
@Getter
@Setter
@SequenceGenerator(name = "lectures_seq", sequenceName = "lectures_seq", allocationSize = 1)
public class Lecture extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lectures_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 50)
    private String videoId;

    @Column(columnDefinition = "TEXT")
    private String content;
}
