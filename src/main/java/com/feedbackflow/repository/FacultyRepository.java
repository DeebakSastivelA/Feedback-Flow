package com.feedbackflow.repository;

import com.feedbackflow.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    boolean existsByEmailIgnoreCase(String email);
    java.util.Optional<Faculty> findByEmailIgnoreCase(String email);
}