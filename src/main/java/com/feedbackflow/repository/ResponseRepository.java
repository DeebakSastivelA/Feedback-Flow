package com.feedbackflow.repository;

import com.feedbackflow.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponseRepository extends JpaRepository<Response, Long> {}