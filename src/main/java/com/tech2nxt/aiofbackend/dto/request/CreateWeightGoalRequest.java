package com.tech2nxt.aiofbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create weight goal")
public class CreateWeightGoalRequest {

    @NotNull(message = "Starting weight is required")
    @DecimalMin(value = "0.1", message = "Starting weight must be greater than 0")
    @Schema(description = "Starting weight", example = "80.0", required = true)
    private BigDecimal startingWeight;

    @NotNull(message = "Target weight is required")
    @DecimalMin(value = "0.1", message = "Target weight must be greater than 0")
    @Schema(description = "Target weight", example = "75.0", required = true)
    private BigDecimal targetWeight;

    @NotNull(message = "Start date is required")
    @Schema(description = "Goal start date", example = "2025-11-01", required = true)
    private LocalDate startDate;

    @NotNull(message = "Target date is required")
    @Schema(description = "Target completion date", example = "2026-02-01", required = true)
    private LocalDate targetDate;

    @Schema(description = "Notes about the goal", example = "Want to get healthier")
    private String notes;
}