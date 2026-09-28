package com.feedbackflow.controller;

import com.feedbackflow.dto.ApiResponses;
import com.feedbackflow.dto.SubmitFeedbackRequest;
import com.feedbackflow.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class FeedbackController {
    private final FeedbackService service;
    public FeedbackController(FeedbackService service) { this.service = service; }

    @GetMapping("/api/student/{studentId}/forms")
    public ApiResponses.StudentForms forms(@PathVariable Long studentId) { return service.formsForStudent(studentId); }
    @GetMapping("/api/student/{studentId}/submissions")
    public List<ApiResponses.Submission> submissions(@PathVariable Long studentId) { return service.submissionsForStudent(studentId); }
    @PostMapping("/api/forms/{formId}/submissions")
    public ResponseEntity<ApiResponses.Message> submit(@PathVariable Long formId, @Valid @RequestBody SubmitFeedbackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.submit(formId, request));
    }
}