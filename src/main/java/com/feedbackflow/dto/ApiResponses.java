package com.feedbackflow.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public final class ApiResponses {
    private ApiResponses() {}

    public record Student(Long id, String name, String rollNumber, String email, String department,
            List<Long> courseIds, List<String> assignedCourses) {}
    public record Faculty(Long id, String name, String email, String department, List<Long> courseIds,
            List<String> assignedCourses) {}
    public record Course(Long id, String courseCode, String title, Long facultyId, String facultyName) {}
    public record Question(Long id, String questionText, int displayOrder, int minRating, int maxRating) {}
    public record Form(Long id, Long courseId, String courseCode, String courseTitle, String semester,
            LocalDate closingDate, String status, long submittedCount, List<Question> questions) {}
    public record Login(String role, Long id, String name, String email, String department,
            List<Course> courses) {}
    public record StudentForms(Long studentId, String studentName, List<Form> forms) {}
    public record SubmittedAnswer(Long questionId, String questionText, int rating) {}
    public record Submission(Long submissionId, Long formId, String courseCode, String courseTitle,
            String semester, LocalDateTime submittedAt, List<SubmittedAnswer> answers) {}
    public record Message(String message) {}
        public record ValidationError(String message, Map<String, String> errors) {}
}