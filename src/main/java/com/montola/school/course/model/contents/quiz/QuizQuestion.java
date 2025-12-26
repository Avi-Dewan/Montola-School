package com.montola.school.course.model.contents.quiz;

import com.montola.school.common.model.Persistent;
import com.montola.school.course.enums.QuestionType;
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
@Table(name = "quiz_questions")
@Getter
@Setter
@SequenceGenerator(name = "quiz_questions_seq", sequenceName = "quiz_questions_seq", allocationSize = 2)
public class QuizQuestion extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quiz_questions_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name="question_type", nullable = false, length = 50)
    private QuestionType type;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Column(nullable = false)
    private int marks;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<QuizOption> options;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private QuizWrittenAnswer writtenAnswer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<QuizFillBlank> fillBlanks;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<QuizTableMatching> tableMatchings;

    public void addOption(QuizOption option) {
        if (options == null) {
            options = new java.util.ArrayList<>();
        }

        options.add(option);
        option.setQuestion(this);
    }

    public void setWrittenAnswer(QuizWrittenAnswer writtenAnswer) {
        this.writtenAnswer = writtenAnswer;

        if (writtenAnswer != null) {
            writtenAnswer.setQuestion(this);
        }
    }

    public void addFillBlank(QuizFillBlank fillBlank) {
        if (fillBlanks == null) {
            fillBlanks = new java.util.ArrayList<>();
        }

        fillBlanks.add(fillBlank);
        fillBlank.setQuestion(this);
    }

    public void addTableMatching(QuizTableMatching tableMatching) {
        if (tableMatchings == null) {
            tableMatchings = new java.util.ArrayList<>();
        }

        tableMatchings.add(tableMatching);
        tableMatching.setQuestion(this);
    }
}
