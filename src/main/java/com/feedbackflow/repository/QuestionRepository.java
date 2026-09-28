package com.feedbackflow.repository;

import com.feedbackflow.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByFeedbackFormIdOrderByDisplayOrder(Long formId);
}