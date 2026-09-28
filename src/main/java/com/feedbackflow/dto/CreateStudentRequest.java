package com.feedbackflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record CreateStudentRequest(
                @NotBlank @Pattern(regexp = "^(?=.*\\p{L})[\\p{L} .-]+$") String name,
                @NotBlank @Pattern(regexp = "^(CS|ME|CE|EC|EE)[0-9]{3}$") String rollNumber,
                @NotBlank @Pattern(regexp = "^[A-Za-z0-9._%+-]+@stu\\.edu\\.in$") String email,
                @NotBlank String department,
                @NotBlank @Size(min = 6) @Pattern(regexp = "^\\S.*\\S$") String password,
                @NotNull @Positive Long assignedCourseId) {
        public CreateStudentRequest {
                if (name != null) name = name.trim();
                if (rollNumber != null) rollNumber = rollNumber.toUpperCase(Locale.ROOT);
                if (email != null) email = email.toLowerCase(Locale.ROOT);
        }
}