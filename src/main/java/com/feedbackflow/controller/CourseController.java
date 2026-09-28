package com.feedbackflow.controller;

import com.feedbackflow.dto.ApiResponses;
import com.feedbackflow.dto.CreateCourseRequest;
import com.feedbackflow.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/courses")
public class CourseController {
    private final CourseService service;
    public CourseController(CourseService service) { this.service = service; }
    @PostMapping
    public ResponseEntity<ApiResponses.Course> create(@Valid @RequestBody CreateCourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
    @GetMapping
    public List<ApiResponses.Course> getAll() { return service.getAll(); }
}