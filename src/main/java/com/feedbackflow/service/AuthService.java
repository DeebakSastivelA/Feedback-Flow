package com.feedbackflow.service;

import com.feedbackflow.dto.ApiResponses;
import com.feedbackflow.dto.LoginRequest;
import com.feedbackflow.exception.InvalidCredentialsException;
import com.feedbackflow.repository.FacultyRepository;
import com.feedbackflow.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AuthService {
    private final StudentRepository students;
    private final FacultyRepository faculties;

    public AuthService(StudentRepository students, FacultyRepository faculties) { this.students = students; this.faculties = faculties; }

    public ApiResponses.Login login(LoginRequest request) {
        if ("STUDENT".equalsIgnoreCase(request.role())) {
            var student = students.findByEmailIgnoreCaseOrRollNumberIgnoreCase(request.identifier(), request.identifier())
                    .filter(person -> person.getPassword().equals(request.password()))
                    .orElseThrow(() -> new InvalidCredentialsException("Email/roll number or password is incorrect."));
            List<ApiResponses.Course> assigned = student.getStudentCourses().stream().map(link -> ApiMapper.course(link.getCourse())).toList();
            return new ApiResponses.Login("STUDENT", student.getId(), student.getName(), student.getEmail(), student.getDepartment(), assigned);
        }
        if ("FACULTY".equalsIgnoreCase(request.role())) {
            var faculty = faculties.findByEmailIgnoreCase(request.identifier()).filter(person -> person.getPassword().equals(request.password()))
                    .orElseThrow(() -> new InvalidCredentialsException("Email or password is incorrect."));
            List<ApiResponses.Course> assigned = faculty.getCourses().stream().map(ApiMapper::course).toList();
            return new ApiResponses.Login("FACULTY", faculty.getId(), faculty.getName(), faculty.getEmail(), faculty.getDepartment(), assigned);
        }
        throw new InvalidCredentialsException("Choose Student or Faculty to sign in.");
    }
}