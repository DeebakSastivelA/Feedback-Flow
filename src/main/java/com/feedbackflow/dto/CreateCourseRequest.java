package com.feedbackflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Locale;

public record CreateCourseRequest(@NotBlank String courseCode, @NotBlank String title,
		@NotNull @Positive Long facultyId) {
	public CreateCourseRequest {
		if (courseCode != null) courseCode = courseCode.trim().toUpperCase(Locale.ROOT);
		if (title != null) title = title.trim();
	}
}