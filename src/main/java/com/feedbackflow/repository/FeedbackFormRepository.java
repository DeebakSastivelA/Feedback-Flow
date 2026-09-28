package com.feedbackflow.repository;

import com.feedbackflow.entity.FeedbackForm;
import com.feedbackflow.entity.FormStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackFormRepository extends JpaRepository<FeedbackForm, Long> {
    List<FeedbackForm> findAllByOrderByCreatedAtDesc();
    List<FeedbackForm> findByCourseFacultyIdOrderByCreatedAtDesc(Long facultyId);
    List<FeedbackForm> findByCourseIdOrderByCreatedAtDesc(Long courseId);
    List<FeedbackForm> findByCourseIdAndSemesterIgnoreCaseAndStatus(Long courseId, String semester, FormStatus status);
}