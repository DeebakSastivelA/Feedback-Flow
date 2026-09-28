package com.feedbackflow.entity;

import jakarta.persistence.*;

@Entity
public class Question {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "feedback_form_id", nullable = false)
    private FeedbackForm feedbackForm;
    @Column(nullable = false, length = 500) private String questionText;
    @Column(nullable = false) private int displayOrder;
    @Column(nullable = false) private int minRating = 1;
    @Column(nullable = false) private int maxRating = 5;

    protected Question() {}
    public Question(FeedbackForm form, String questionText, int displayOrder) {
        this.feedbackForm = form; this.questionText = questionText; this.displayOrder = displayOrder;
    }
    public Long getId() { return id; }
    public FeedbackForm getFeedbackForm() { return feedbackForm; }
    public String getQuestionText() { return questionText; }
    public int getDisplayOrder() { return displayOrder; }
    public int getMinRating() { return minRating; }
    public int getMaxRating() { return maxRating; }
}