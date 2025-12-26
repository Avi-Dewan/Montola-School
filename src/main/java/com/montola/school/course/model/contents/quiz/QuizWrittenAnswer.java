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
@Table(name = "quiz_written_answers")
@Getter
@Setter
@SequenceGenerator(name = "quiz_written_answers_seq", sequenceName = "quiz_written_answers_seq", allocationSize = 2)
public class QuizWrittenAnswer extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quiz_written_answers_seq")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    @Column(name = "sample_answer", columnDefinition = "TEXT")
    private String sampleAnswer;
}
