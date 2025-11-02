package com.tech2nxt.aiofbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create new workout request")
public class CreateWorkoutRequest {

    @NotBlank(message = "Workout name is required")
    @Size(min = 2, max = 255, message = "Workout name must be between 2 and 255 characters")
    @Schema(description = "Workout name", example = "Upper Body Day")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Workout description", example = "Focus on chest and back")
    private String description;

    @NotEmpty(message = "At least one exercise is required")
    @Valid
    @Schema(description = "List of exercises in the workout")
    private List<WorkoutExerciseRequest> exercises;

    @Schema(description = "Days to schedule this workout",
            example = "[\"MONDAY\", \"WEDNESDAY\", \"FRIDAY\"]")
    private List<String> scheduledDays; // e.g., ["MONDAY", "WEDNESDAY"]
}
