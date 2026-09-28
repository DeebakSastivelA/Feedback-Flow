package com.feedbackflow.service;

import com.feedbackflow.dto.ApiResponses;
import com.feedbackflow.dto.CreateFeedbackFormRequest;
import com.feedbackflow.entity.FeedbackForm;
import com.feedbackflow.entity.Question;
import com.feedbackflow.exception.ResourceNotFoundException;
import com.feedbackflow.exception.ResourceConflictException;
import com.feedbackflow.repository.CourseRepository;
import com.feedbackflow.repository.FeedbackFormRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDate;

@Service
@Transactional
public class FeedbackFormService {
    public static final List<String> FIXED_QUESTIONS = List.of(
            "The instructor explained course concepts clearly.",
            "The instructor encouraged questions and student participation.",
            "Course materials and assignments supported my learning.",
            "The course was well organized and expectations were clear.",
            "Feedback on my work was timely and helpful.",
            "Overall, this course contributed meaningfully to my academic learning.");
    private final FeedbackFormRepository forms;
    private final CourseRepository courses;

    public FeedbackFormService(FeedbackFormRepository forms, CourseRepository courses) { this.forms = forms; this.courses = courses; }

    public ApiResponses.Form create(CreateFeedbackFormRequest request) {
        if (request.closingDate().isBefore(LocalDate.now())) throw new IllegalArgumentException("Closing date cannot be before today.");
        var course = courses.findById(request.courseId()).orElseThrow(() -> new ResourceNotFoundException("Course not found."));
        boolean activeDuplicate = forms.findByCourseIdAndSemesterIgnoreCaseAndStatus(course.getId(), request.semester(), com.feedbackflow.entity.FormStatus.OPEN)
            .stream().anyMatch(form -> !form.getClosingDate().isBefore(LocalDate.now()));
        if (activeDuplicate) throw new ResourceConflictException("An open feedback form already exists for this course and semester.");
        FeedbackForm form = new FeedbackForm(course, request.semester(), request.closingDate());
        for (int i = 0; i < FIXED_QUESTIONS.size(); i++) form.getQuestions().add(new Question(form, FIXED_QUESTIONS.get(i), i + 1));
        return ApiMapper.form(forms.save(form), false);
    }

    @Transactional(readOnly = true)
    public List<ApiResponses.Form> getAll() { return forms.findAllByOrderByCreatedAtDesc().stream().map(form -> ApiMapper.form(form, false)).toList(); }

    public ApiResponses.Form close(Long id) {
        FeedbackForm form = forms.findById(id).orElseThrow(() -> new ResourceNotFoundException("Feedback form not found."));
        form.close();
        return ApiMapper.form(forms.save(form), false);
    }
}