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
@Table(name = "quiz_options")
@Getter
@Setter
@SequenceGenerator(name = "quiz_options_seq", sequenceName = "quiz_options_seq", allocationSize = 1)
public class QuizOption extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quiz_options_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    @Column(name = "option_text", columnDefinition = "TEXT", nullable = false)
    private String optionText;

    @Column(name = "is_correct")
    private boolean isCorrect;
}

