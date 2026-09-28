package com.feedbackflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Locale;

public record CreateFacultyRequest(
                @NotBlank @Pattern(regexp = "^(?=.*\\p{L})[\\p{L} .-]+$") String name,
                @NotBlank @Pattern(regexp = "^[A-Za-z0-9._%+-]+@prof\\.edu\\.in$") String email,
                @NotBlank String department,
                @NotBlank @Size(min = 6) @Pattern(regexp = "^\\S.*\\S$") String password,
                List<Long> assignedCourseIds) {
        public CreateFacultyRequest {
                if (name != null) name = name.trim();
                if (email != null) email = email.toLowerCase(Locale.ROOT);
        }
}