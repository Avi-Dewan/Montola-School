package com.montola.school.course.model.contents.quiz;

import com.montola.school.common.model.Persistent;
import com.montola.school.course.model.contents.Quiz;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author avidewan
 * @date 10/21/25
 */
@Entity
@Table(name = "quiz_table_matching")
@Getter
@Setter
@SequenceGenerator(name = "quiz_table_matching_seq", sequenceName = "quiz_table_matching_seq", allocationSize = 1)
public class QuizTableMatching extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quiz_table_matching_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    @Column(name = "left_item", length = 255, nullable = false)
    private String leftItem;

    @Column(name = "right_item", length = 255, nullable = false)
    private String rightItem;

    @Column(name = "order_index")
    private int orderIndex;
}

