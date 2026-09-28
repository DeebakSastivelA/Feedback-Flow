package com.feedbackflow.service;

import com.feedbackflow.dto.FeedbackSummaryResponse;
import com.feedbackflow.entity.Response;
import com.feedbackflow.exception.ResourceNotFoundException;
import com.feedbackflow.repository.FacultyRepository;
import com.feedbackflow.repository.FeedbackFormRepository;
import com.feedbackflow.repository.FeedbackSubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class FacultyService {
    private final FacultyRepository faculties;
    private final FeedbackFormRepository forms;
    private final FeedbackSubmissionRepository submissions;

    public FacultyService(FacultyRepository faculties, FeedbackFormRepository forms, FeedbackSubmissionRepository submissions) {
        this.faculties = faculties; this.forms = forms; this.submissions = submissions;
    }

    public FeedbackSummaryResponse summary(Long facultyId) {
        var faculty = faculties.findById(facultyId).orElseThrow(() -> new ResourceNotFoundException("Faculty member not found."));
        List<FeedbackSummaryResponse.CourseSummary> courseSummaries = faculty.getCourses().stream().map(course -> {
            var courseForms = forms.findByCourseIdOrderByCreatedAtDesc(course.getId());
            List<Response> allResponses = courseForms.stream().flatMap(form -> submissions.findByFeedbackFormId(form.getId()).stream())
                    .flatMap(submission -> submission.getResponses().stream()).toList();
            Map<String, List<Integer>> ratings = new LinkedHashMap<>();
            allResponses.forEach(response -> ratings.computeIfAbsent(response.getQuestion().getQuestionText(), key -> new ArrayList<>()).add(response.getRating()));
            courseForms.stream().flatMap(form -> form.getQuestions().stream()).forEach(question -> ratings.computeIfAbsent(question.getQuestionText(), key -> new ArrayList<>()));
            List<FeedbackSummaryResponse.QuestionAverage> averages = ratings.entrySet().stream().map(entry ->
                    new FeedbackSummaryResponse.QuestionAverage(entry.getKey(), average(entry.getValue()))).toList();
            var lowest = averages.stream().min(Comparator.comparingDouble(FeedbackSummaryResponse.QuestionAverage::averageRating)).orElse(null);
            double overall = allResponses.isEmpty() ? 0 : allResponses.stream().mapToInt(Response::getRating).average().orElse(0);
            long count = courseForms.stream().mapToLong(form -> submissions.countByFeedbackFormId(form.getId())).sum();
            return new FeedbackSummaryResponse.CourseSummary(course.getId(), course.getCourseCode(), course.getTitle(), count,
                    overall, lowest == null || allResponses.isEmpty() ? "No feedback yet" : lowest.question(),
                    lowest == null ? 0 : lowest.averageRating(), averages);
        }).toList();
        return new FeedbackSummaryResponse(faculty.getId(), faculty.getName(), courseSummaries);
    }

    private double average(List<Integer> ratings) { return ratings.stream().mapToInt(Integer::intValue).average().orElse(0); }
}