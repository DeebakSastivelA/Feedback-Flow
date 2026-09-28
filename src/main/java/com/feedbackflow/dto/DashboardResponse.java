package com.feedbackflow.dto;

public record DashboardResponse(long totalStudents, long totalFaculties, long totalCourses,
        long totalFeedbackForms, long totalFeedbackFormsFilled) {}