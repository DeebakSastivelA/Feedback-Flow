package com.feedbackflow.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record SubmitFeedbackRequest(@NotNull @Positive Long studentId, @NotEmpty List<@Valid AnswerRequest> answers) {}