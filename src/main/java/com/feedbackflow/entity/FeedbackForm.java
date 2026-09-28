package com.feedbackflow.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class FeedbackForm {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    @Column(nullable = false) private String semester;
    @Column(nullable = false) private LocalDate closingDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private FormStatus status = FormStatus.OPEN;
    @Column(nullable = false) private LocalDateTime createdAt;
    @OneToMany(mappedBy = "feedbackForm", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC") private List<Question> questions = new ArrayList<>();
    @OneToMany(mappedBy = "feedbackForm") private List<FeedbackSubmission> submissions = new ArrayList<>();

    protected FeedbackForm() {}
    public FeedbackForm(Course course, String semester, LocalDate closingDate) {
        this.course = course; this.semester = semester; this.closingDate = closingDate; this.createdAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public String getSemester() { return semester; }
    public LocalDate getClosingDate() { return closingDate; }
    public FormStatus getStatus() { return status; }
    public void close() { status = FormStatus.CLOSED; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<Question> getQuestions() { return questions; }
    public List<FeedbackSubmission> getSubmissions() { return submissions; }
}