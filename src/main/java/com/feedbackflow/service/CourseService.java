package com.feedbackflow.service;

import com.feedbackflow.dto.ApiResponses;
import com.feedbackflow.dto.CreateCourseRequest;
import com.feedbackflow.entity.Course;
import com.feedbackflow.exception.ResourceNotFoundException;
import com.feedbackflow.exception.ResourceConflictException;
import com.feedbackflow.repository.CourseRepository;
import com.feedbackflow.repository.FacultyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class CourseService {
    private final CourseRepository courses;
    private final FacultyRepository faculties;

    public CourseService(CourseRepository courses, FacultyRepository faculties) { this.courses = courses; this.faculties = faculties; }

    public ApiResponses.Course create(CreateCourseRequest request) {
        String courseCode = request.courseCode().trim().toUpperCase(Locale.ROOT);
        String title = request.title().trim();
        if (title.isEmpty()) throw new IllegalArgumentException("Course title is required.");
        if (courses.existsByCourseCodeIgnoreCase(courseCode)) throw new ResourceConflictException("A course with this code already exists.");
        var faculty = faculties.findById(request.facultyId()).orElseThrow(() -> new ResourceNotFoundException("Faculty member not found."));
        return ApiMapper.course(courses.save(new Course(courseCode, title, faculty)));
    }

    @Transactional(readOnly = true)
    public List<ApiResponses.Course> getAll() { return courses.findAll().stream().map(ApiMapper::course).toList(); }
}