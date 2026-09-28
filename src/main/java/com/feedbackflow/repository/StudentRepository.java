package com.feedbackflow.repository;

import com.feedbackflow.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByRollNumberIgnoreCase(String rollNumber);
    Optional<Student> findByEmailIgnoreCaseOrRollNumberIgnoreCase(String email, String rollNumber);
}