package com.feedbackflow.service;

import com.feedbackflow.dto.*;
import com.feedbackflow.dto.ApiResponses;
import com.feedbackflow.entity.*;
import com.feedbackflow.exception.DuplicateFeedbackException;
import com.feedbackflow.exception.FormClosedException;
import com.feedbackflow.exception.ResourceNotFoundException;
import com.feedbackflow.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class FeedbackService {
    private final StudentRepository students;
    private final StudentCourseRepository studentCourses;
    private final FeedbackFormRepository forms;
    private final QuestionRepository questions;
    private final FeedbackSubmissionRepository submissions;

    public FeedbackService(StudentRepository students, StudentCourseRepository studentCourses,
            FeedbackFormRepository forms, QuestionRepository questions, FeedbackSubmissionRepository submissions) {
        this.students = students; this.studentCourses = studentCourses; this.forms = forms; this.questions = questions; this.submissions = submissions;
    }

    @Transactional(readOnly = true)
    public ApiResponses.StudentForms formsForStudent(Long studentId) {
        var student = students.findById(studentId).orElseThrow(() -> new ResourceNotFoundException("Student not found."));
        Set<Long> courseIds = new HashSet<>(studentCourses.findByStudentId(studentId).stream().map(link -> link.getCourse().getId()).toList());
        List<ApiResponses.Form> available = forms.findAllByOrderByCreatedAtDesc().stream()
                .filter(form -> courseIds.contains(form.getCourse().getId()))
                .map(form -> ApiMapper.form(form, submissions.existsByStudentIdAndFeedbackFormId(studentId, form.getId()))).toList();
        return new ApiResponses.StudentForms(studentId, student.getName(), available);
    }

    @Transactional(readOnly = true)
    public List<ApiResponses.Submission> submissionsForStudent(Long studentId) {
        if (!students.existsById(studentId)) throw new ResourceNotFoundException("Student not found.");
        return submissions.findByStudentIdOrderBySubmittedAtDesc(studentId).stream().map(submission ->
                new ApiResponses.Submission(submission.getId(), submission.getFeedbackForm().getId(),
                        submission.getFeedbackForm().getCourse().getCourseCode(), submission.getFeedbackForm().getCourse().getTitle(),
                        submission.getFeedbackForm().getSemester(), submission.getSubmittedAt(),
                        submission.getResponses().stream().map(response -> new ApiResponses.SubmittedAnswer(response.getQuestion().getId(),
                                response.getQuestion().getQuestionText(), response.getRating())).toList())).toList();
    }

    public ApiResponses.Message submit(Long formId, SubmitFeedbackRequest request) {
        Student student = students.findById(request.studentId()).orElseThrow(() -> new ResourceNotFoundException("Student not found."));
        FeedbackForm form = forms.findById(formId).orElseThrow(() -> new ResourceNotFoundException("Feedback form not found."));
        if (!studentCourses.existsByStudentIdAndCourseId(student.getId(), form.getCourse().getId()))
            throw new IllegalArgumentException("You can submit feedback only for a course assigned to you.");
        if (submissions.existsByStudentIdAndFeedbackFormId(student.getId(), formId))
            throw new DuplicateFeedbackException("You have already submitted feedback for this form.");
        if (form.getStatus() == FormStatus.CLOSED || LocalDate.now().isAfter(form.getClosingDate()))
            throw new FormClosedException("This feedback form is closed and no longer accepts submissions.");

        List<Question> formQuestions = questions.findByFeedbackFormIdOrderByDisplayOrder(formId);
        Map<Long, AnswerRequest> answers = new HashMap<>();
        for (AnswerRequest answer : request.answers()) {
            if (answer.rating().stripTrailingZeros().scale() > 0) throw new IllegalArgumentException("Ratings must be whole numbers from 1 to 5.");
            if (answers.putIfAbsent(answer.questionId(), answer) != null) throw new IllegalArgumentException("Each question must have exactly one rating.");
        }
        Set<Long> requiredQuestionIds = new HashSet<>(formQuestions.stream().map(Question::getId).toList());
        if (formQuestions.size() != 6 || answers.size() != 6 || !answers.keySet().equals(requiredQuestionIds))
            throw new IllegalArgumentException("Please answer all six questions exactly once.");

        FeedbackSubmission submission = new FeedbackSubmission(student, form);
        for (Question question : formQuestions)
            submission.getResponses().add(new Response(submission, question, answers.get(question.getId()).rating().intValueExact()));
        submissions.save(submission);
        return new ApiResponses.Message("Feedback submitted successfully.");
    }
}