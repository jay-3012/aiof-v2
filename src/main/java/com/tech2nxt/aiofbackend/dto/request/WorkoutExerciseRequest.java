package com.tech2nxt.aiofbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Exercise to add to workout")
public class WorkoutExerciseRequest {

    @NotBlank(message = "Exercise ID is required")
    @Schema(description = "Exercise ID from wger API", example = "123")
    private String exerciseId;

    @NotBlank(message = "Exercise name is required")
    @Schema(description = "Exercise name", example = "Bench Press")
    private String exerciseName;

    @Schema(description = "Muscle group targeted", example = "Chest")
    private String muscleGroup;

    @Schema(description = "Equipment needed", example = "Barbell")
    private String equipment;

    @Schema(description = "Number of sets", example = "3")
    private Integer sets;

    @Schema(description = "Number of reps per set", example = "10")
    private Integer reps;

    @Schema(description = "Weight in kg or lbs", example = "60.5")
    private BigDecimal weight;

    @Schema(description = "Notes for this exercise", example = "Focus on form")
    private String notes;

    @NotNull(message = "Order index is required")
    @Schema(description = "Order in the workout (0-indexed)", example = "0")
    private Integer orderIndex;
}
