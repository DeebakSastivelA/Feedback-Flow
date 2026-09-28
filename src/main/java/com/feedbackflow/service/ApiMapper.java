package com.feedbackflow.service;

import com.feedbackflow.dto.ApiResponses;
import com.feedbackflow.entity.*;
import java.time.LocalDate;
import java.util.List;

final class ApiMapper {
    private ApiMapper() {}

    static ApiResponses.Course course(Course course) {
        return new ApiResponses.Course(course.getId(), course.getCourseCode(), course.getTitle(),
                course.getFaculty().getId(), course.getFaculty().getName());
    }

    static ApiResponses.Form form(FeedbackForm form, boolean submitted) {
        String status = submitted ? "SUBMITTED" : form.getStatus() == FormStatus.CLOSED || LocalDate.now().isAfter(form.getClosingDate()) ? "CLOSED" : "OPEN";
        List<ApiResponses.Question> questions = form.getQuestions().stream()
                .map(q -> new ApiResponses.Question(q.getId(), q.getQuestionText(), q.getDisplayOrder(), q.getMinRating(), q.getMaxRating())).toList();
        return new ApiResponses.Form(form.getId(), form.getCourse().getId(), form.getCourse().getCourseCode(),
                form.getCourse().getTitle(), form.getSemester(), form.getClosingDate(), status, form.getSubmissions().size(), questions);
    }
}