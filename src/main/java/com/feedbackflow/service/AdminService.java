package com.feedbackflow.service;

import com.feedbackflow.dto.*;
import com.feedbackflow.dto.ApiResponses;
import com.feedbackflow.entity.*;
import com.feedbackflow.exception.ResourceNotFoundException;
import com.feedbackflow.exception.ResourceConflictException;
import com.feedbackflow.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional
public class AdminService {
    private static final Map<String, String> DEPARTMENT_PREFIXES = Map.of(
            "Computer Science and Engineering", "CS",
            "Mechanical Engineering", "ME",
            "Civil Engineering", "CE",
            "Electronics and communication engineering", "EC",
            "Electronics and Electrical Engineering", "EE");
    private final StudentRepository students;
    private final FacultyRepository faculties;
    private final CourseRepository courses;
    private final StudentCourseRepository studentCourses;
    private final FeedbackFormRepository forms;
    private final FeedbackSubmissionRepository submissions;

    public AdminService(StudentRepository students, FacultyRepository faculties, CourseRepository courses,
            StudentCourseRepository studentCourses, FeedbackFormRepository forms, FeedbackSubmissionRepository submissions) {
        this.students = students; this.faculties = faculties; this.courses = courses; this.studentCourses = studentCourses;
        this.forms = forms; this.submissions = submissions;
    }

    public ApiResponses.Student createStudent(CreateStudentRequest request) {
        validateDepartment(request.department());
        String rollNumber = request.rollNumber().toUpperCase(Locale.ROOT);
        String email = request.email().toLowerCase(Locale.ROOT);
        if (!rollNumber.matches("^[A-Z]{2}[0-9]{3}$") || !rollNumber.startsWith(DEPARTMENT_PREFIXES.get(request.department())))
            throw new IllegalArgumentException("Roll number must use the selected department prefix followed by exactly three digits.");
        if (!email.matches("^[A-Za-z0-9._%+-]+@stu\\.edu\\.in$"))
            throw new IllegalArgumentException("Student email must use the format name@stu.edu.in.");
        if (students.existsByEmailIgnoreCase(email) || students.existsByRollNumberIgnoreCase(rollNumber))
            throw new ResourceConflictException("A student with this email or roll number already exists.");
        Course course = courses.findById(request.assignedCourseId()).orElseThrow(() -> new ResourceNotFoundException("Course not found."));
        Student student = students.save(new Student(request.name().trim(), rollNumber, email, request.department(), request.password()));
        studentCourses.save(new StudentCourse(student, course));
        return student(student);
    }

    @Transactional(readOnly = true)
    public List<ApiResponses.Student> getStudents() { return students.findAll().stream().map(this::student).toList(); }

    public void deleteStudent(Long id) {
        Student student = students.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student not found."));
        if (submissions.existsByStudentId(id)) throw new IllegalStateException("This student has submitted feedback and cannot be deleted.");
        students.delete(student);
    }

    public ApiResponses.Faculty createFaculty(CreateFacultyRequest request) {
        validateDepartment(request.department());
        String email = request.email().toLowerCase(Locale.ROOT);
        if (!email.matches("^[A-Za-z0-9._%+-]+@prof\\.edu\\.in$"))
            throw new IllegalArgumentException("Faculty email must use the format name@prof.edu.in.");
        if (faculties.existsByEmailIgnoreCase(email)) throw new ResourceConflictException("A faculty member with this email already exists.");
        Faculty faculty = faculties.save(new Faculty(request.name().trim(), email, request.department(), request.password()));
        if (request.assignedCourseIds() != null) {
            for (Long courseId : request.assignedCourseIds()) {
                Course course = courses.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course " + courseId + " not found."));
                course.setFaculty(faculty);
                faculty.getCourses().add(course);
                courses.save(course);
            }
        }
        return faculty(faculty);
    }

    @Transactional(readOnly = true)
    public List<ApiResponses.Faculty> getFaculties() { return faculties.findAll().stream().map(this::faculty).toList(); }

    public void deleteFaculty(Long id) {
        Faculty faculty = faculties.findById(id).orElseThrow(() -> new ResourceNotFoundException("Faculty member not found."));
        if (!faculty.getCourses().isEmpty()) throw new IllegalStateException("This faculty member still has assigned courses. Reassign those courses before deleting.");
        faculties.delete(faculty);
    }

    @Transactional(readOnly = true)
    public DashboardResponse dashboard() {
        return new DashboardResponse(students.count(), faculties.count(), courses.count(), forms.count(), submissions.count());
    }

    private ApiResponses.Student student(Student student) {
        List<StudentCourse> assigned = studentCourses.findByStudentId(student.getId());
        return new ApiResponses.Student(student.getId(), student.getName(), student.getRollNumber(), student.getEmail(),
                student.getDepartment(), assigned.stream().map(link -> link.getCourse().getId()).toList(),
                assigned.stream().map(link -> link.getCourse().getCourseCode() + " - " + link.getCourse().getTitle()).toList());
    }

    private ApiResponses.Faculty faculty(Faculty faculty) {
        return new ApiResponses.Faculty(faculty.getId(), faculty.getName(), faculty.getEmail(), faculty.getDepartment(),
                faculty.getCourses().stream().map(Course::getId).toList(),
                faculty.getCourses().stream().map(course -> course.getCourseCode() + " - " + course.getTitle()).toList());
    }

    private void validateDepartment(String department) {
        if (!DEPARTMENT_PREFIXES.containsKey(department)) throw new IllegalArgumentException("Choose one of the supported departments.");
    }
}