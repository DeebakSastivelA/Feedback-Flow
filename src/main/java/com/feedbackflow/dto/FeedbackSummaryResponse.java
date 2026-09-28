package com.feedbackflow.dto;

import java.util.List;

public record FeedbackSummaryResponse(Long facultyId, String facultyName, List<CourseSummary> courses) {
    public record CourseSummary(Long courseId, String courseCode, String courseTitle, long submissions,
            double overallAverage, String lowestRatedQuestion, double lowestQuestionAverage,
            List<QuestionAverage> questions) {}
    public record QuestionAverage(String question, double averageRating) {}
}