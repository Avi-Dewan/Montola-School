package com.montola.school.course.repository;

import com.montola.school.course.model.contents.quiz.QuizOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author avidewan
 * @date 11/29/25
 */
@Repository
public interface QuizOptionRepository extends JpaRepository<QuizOption, Long> {

    List<QuizOption> findByQuestionId(Long questionId);
}
