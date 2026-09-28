package com.feedbackflow.controller;

import com.feedbackflow.dto.ApiResponses;
import com.feedbackflow.dto.CreateFeedbackFormRequest;
import com.feedbackflow.service.FeedbackFormService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class FeedbackFormController {
    private final FeedbackFormService service;
    public FeedbackFormController(FeedbackFormService service) { this.service = service; }
    @PostMapping("/api/admin/forms")
    public ResponseEntity<ApiResponses.Form> create(@Valid @RequestBody CreateFeedbackFormRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
    @GetMapping("/api/admin/forms")
    public List<ApiResponses.Form> getAll() { return service.getAll(); }
    @PutMapping("/api/forms/{id}/close")
    public ApiResponses.Form close(@PathVariable Long id) { return service.close(id); }
}