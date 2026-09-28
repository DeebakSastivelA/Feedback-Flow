package com.feedbackflow.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_submission_student_form", columnNames = {"student_id", "feedback_form_id"}))
public class FeedbackSubmission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    @ManyToOne(optional = false) @JoinColumn(name = "feedback_form_id", nullable = false)
    private FeedbackForm feedbackForm;
    @Column(nullable = false) private LocalDateTime submittedAt;
    @OneToMany(mappedBy = "feedbackSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Response> responses = new ArrayList<>();

    protected FeedbackSubmission() {}
    public FeedbackSubmission(Student student, FeedbackForm form) { this.student = student; this.feedbackForm = form; this.submittedAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public FeedbackForm getFeedbackForm() { return feedbackForm; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public List<Response> getResponses() { return responses; }
}