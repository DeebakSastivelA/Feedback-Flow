package com.feedbackflow.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record AnswerRequest(@NotNull @Positive Long questionId,
	@NotNull @DecimalMin("1") @DecimalMax("5") BigDecimal rating) {}