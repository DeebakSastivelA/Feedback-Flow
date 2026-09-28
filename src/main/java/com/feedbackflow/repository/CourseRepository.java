package com.feedbackflow.repository;

import com.feedbackflow.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByCourseCodeIgnoreCase(String courseCode);
}