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
@Schema(description = "Log body weight")
public class LogWeightRequest {

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.1", message = "Weight must be greater than 0")
    @Schema(description = "Body weight in kg or lbs", example = "75.5", required = true)
    private BigDecimal weight;

    @Schema(description = "Log date (defaults to today if not provided)", example = "2025-11-02")
    private LocalDate logDate;

    @Schema(description = "Optional notes", example = "Feeling good!")
    private String notes;
}