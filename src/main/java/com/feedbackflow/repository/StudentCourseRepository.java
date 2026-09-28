package com.feedbackflow.repository;

import com.feedbackflow.entity.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    List<StudentCourse> findByStudentId(Long studentId);
}