package com.feedbackflow.controller;

import com.feedbackflow.dto.FeedbackSummaryResponse;
import com.feedbackflow.service.FacultyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/faculty")
public class FacultyController {
    private final FacultyService service;
    public FacultyController(FacultyService service) { this.service = service; }
    @GetMapping("/{facultyId}/feedback-summary")
    public FeedbackSummaryResponse summary(@PathVariable Long facultyId) { return service.summary(facultyId); }
}