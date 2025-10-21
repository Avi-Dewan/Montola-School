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
@Table(name = "quiz_fill_blanks")
@Getter
@Setter
@SequenceGenerator(name = "quiz_fill_blanks_seq", sequenceName = "quiz_fill_blanks_seq", allocationSize = 1)
public class QuizFillBlank extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quiz_fill_blanks_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    @Column(nullable = false)
    private int position;

    @Column(name = "correct_answer", length = 255, nullable = false)
    private String correctAnswer;
}

