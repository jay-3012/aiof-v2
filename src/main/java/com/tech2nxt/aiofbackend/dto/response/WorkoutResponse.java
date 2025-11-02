package com.tech2nxt.aiofbackend.dto.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tech2nxt.aiofbackend.model.Workout;
import com.tech2nxt.aiofbackend.model.WorkoutExercise;
import com.tech2nxt.aiofbackend.model.WorkoutSchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Workout details with exercises and schedule")
public class WorkoutResponse {

    @Schema(description = "Workout ID", example = "1")
    private Long id;

    @Schema(description = "Workout name", example = "Upper Body Day")
    private String name;

    @Schema(description = "Workout description", example = "Focus on chest and back")
    private String description;

    @Schema(description = "List of exercises in the workout")
    private List<WorkoutExerciseResponse> exercises;

    @Schema(description = "Scheduled days", example = "[\"MONDAY\", \"WEDNESDAY\"]")
    private List<String> scheduledDays;

    @Schema(description = "Number of exercises", example = "5")
    private Integer exerciseCount;

    @Schema(description = "Creation timestamp", example = "2025-11-01T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2025-11-01T10:30:00")
    private LocalDateTime updatedAt;

    public static WorkoutResponse fromEntity(Workout workout) {
        return WorkoutResponse.builder()
                .id(workout.getId())
                .name(workout.getName())
                .description(workout.getDescription())
                .exercises(workout.getExercises().stream()
                        .map(WorkoutExerciseResponse::fromEntity)
                        .collect(Collectors.toList()))
                .scheduledDays(workout.getSchedules().stream()
                        .map(s -> s.getDayOfWeek().name())
                        .collect(Collectors.toList()))
                .exerciseCount(workout.getExercises().size())
                .createdAt(workout.getCreatedAt())
                .updatedAt(workout.getUpdatedAt())
                .build();
    }

    public static WorkoutResponse fromEntityMinimal(Workout workout) {
        return WorkoutResponse.builder()
                .id(workout.getId())
                .name(workout.getName())
                .description(workout.getDescription())
                .exerciseCount(workout.getExercises().size())
                .createdAt(workout.getCreatedAt())
                .updatedAt(workout.getUpdatedAt())
                .build();
    }
}
