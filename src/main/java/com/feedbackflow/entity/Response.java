package com.feedbackflow.entity;

import jakarta.persistence.*;

@Entity
public class Response {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "feedback_submission_id", nullable = false)
    private FeedbackSubmission feedbackSubmission;
    @ManyToOne(optional = false) @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    @Column(nullable = false) private int rating;

    protected Response() {}
    public Response(FeedbackSubmission submission, Question question, int rating) {
        this.feedbackSubmission = submission; this.question = question; this.rating = rating;
    }
    public Long getId() { return id; }
    public FeedbackSubmission getFeedbackSubmission() { return feedbackSubmission; }
    public Question getQuestion() { return question; }
    public int getRating() { return rating; }
}