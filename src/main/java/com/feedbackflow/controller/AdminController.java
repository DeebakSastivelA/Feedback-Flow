package com.feedbackflow.controller;

import com.feedbackflow.dto.ApiResponses;
import com.feedbackflow.dto.CreateFacultyRequest;
import com.feedbackflow.dto.CreateStudentRequest;
import com.feedbackflow.dto.DashboardResponse;
import com.feedbackflow.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService service;
    public AdminController(AdminService service) { this.service = service; }

    @PostMapping("/students")
    public ResponseEntity<ApiResponses.Student> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createStudent(request));
    }
    @GetMapping("/students")
    public List<ApiResponses.Student> students() { return service.getStudents(); }
    @DeleteMapping("/students/{id}")
    public ApiResponses.Message deleteStudent(@PathVariable Long id) { service.deleteStudent(id); return new ApiResponses.Message("Student deleted."); }

    @PostMapping("/faculties")
    public ResponseEntity<ApiResponses.Faculty> createFaculty(@Valid @RequestBody CreateFacultyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createFaculty(request));
    }
    @GetMapping("/faculties")
    public List<ApiResponses.Faculty> faculties() { return service.getFaculties(); }
    @DeleteMapping("/faculties/{id}")
    public ApiResponses.Message deleteFaculty(@PathVariable Long id) { service.deleteFaculty(id); return new ApiResponses.Message("Faculty member deleted."); }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() { return service.dashboard(); }
}