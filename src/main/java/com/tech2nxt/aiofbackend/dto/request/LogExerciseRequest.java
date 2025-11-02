package com.tech2nxt.aiofbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Log an exercise during active session")
public class LogExerciseRequest {

    @NotBlank(message = "Exercise ID is required")
    @Schema(description = "Exercise ID from wger API", example = "228")
    private String exerciseId;

    @NotBlank(message = "Exercise name is required")
    @Schema(description = "Exercise name", example = "Bench Press")
    private String exerciseName;

    @Schema(description = "Muscle group", example = "Chest")
    private String muscleGroup;

    @Schema(description = "Equipment used", example = "Barbell")
    private String equipment;

    @Schema(description = "Number of sets completed", example = "4")
    private Integer sets;

    @Schema(description = "Number of reps per set", example = "10")
    private Integer reps;

    @Schema(description = "Weight used (kg or lbs)", example = "60.5")
    private BigDecimal weight;

    @Schema(description = "Time spent on this exercise (seconds)", example = "180")
    private Integer durationSeconds;

    @Schema(description = "Notes about this exercise", example = "Felt strong today")
    private String notes;

    @NotNull(message = "Order index is required")
    @Schema(description = "Order in the workout", example = "0")
    private Integer orderIndex;
}

