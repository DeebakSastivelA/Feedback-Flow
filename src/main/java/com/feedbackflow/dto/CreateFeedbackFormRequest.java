package com.feedbackflow.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record CreateFeedbackFormRequest(@NotNull @Positive Long courseId, @NotBlank String semester,
                @NotNull @FutureOrPresent LocalDate closingDate) {
        public CreateFeedbackFormRequest {
                if (semester != null) semester = semester.trim();
        }
}