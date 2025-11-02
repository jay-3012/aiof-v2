package com.tech2nxt.aiofbackend.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
@Schema(description = "Update existing workout request")
public class UpdateWorkoutRequest {

    @Size(min = 2, max = 255, message = "Workout name must be between 2 and 255 characters")
    @Schema(description = "Workout name", example = "Upper Body Day (Updated)")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Workout description", example = "Updated description")
    private String description;

    @Valid
    @Schema(description = "List of exercises (replaces all existing)")
    private List<WorkoutExerciseRequest> exercises;

    @Schema(description = "Days to schedule this workout (replaces all existing)",
            example = "[\"MONDAY\", \"THURSDAY\"]")
    private List<String> scheduledDays;
}
