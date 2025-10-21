package com.montola.school.course.model.contents;

import com.montola.school.common.model.Persistent;
import com.montola.school.course.model.ContentItem;
import com.montola.school.course.model.contents.quiz.QuizQuestion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author avidewan
 * @date 10/21/25
 */
@Entity
@Table(name = "quizzes")
@Getter
@Setter
@SequenceGenerator(name = "quizzes_seq", sequenceName = "quizzes_seq", allocationSize = 1)
public class Quiz extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quizzes_seq")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_item_id", nullable = false)
    private ContentItem contentItem;

    @Column(name = "quiz_type", nullable = false, length = 50)
    private String quizType; // MCQ | WRITTEN | FILL_BLANK | TABLE_MATCHING

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String instruction;

    @Column(name = "time_limit")
    private Integer timeLimit;

    @Column(name = "total_marks")
    private Integer totalMarks;

    @Column(name = "pass_percentage", precision = 5, scale = 2)
    private Double passPercentage = 60.0;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<QuizQuestion> questions;
}
