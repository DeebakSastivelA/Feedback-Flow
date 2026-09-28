package com.feedbackflow.repository;

import com.feedbackflow.entity.FeedbackSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackSubmissionRepository extends JpaRepository<FeedbackSubmission, Long> {
    boolean existsByStudentIdAndFeedbackFormId(Long studentId, Long formId);
    boolean existsByStudentId(Long studentId);
    long countByFeedbackFormId(Long formId);
    List<FeedbackSubmission> findByStudentIdOrderBySubmittedAtDesc(Long studentId);
    List<FeedbackSubmission> findByFeedbackFormId(Long formId);
}