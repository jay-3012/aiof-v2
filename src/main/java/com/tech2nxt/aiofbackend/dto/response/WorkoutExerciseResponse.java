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
@Schema(description = "Exercise details in a workout")
class WorkoutExerciseResponse {

    @Schema(description = "Exercise ID in workout", example = "1")
    private Long id;

    @Schema(description = "Exercise ID from wger API", example = "123")
    private String exerciseId;

    @Schema(description = "Exercise name", example = "Bench Press")
    private String exerciseName;

    @Schema(description = "Muscle group", example = "Chest")
    private String muscleGroup;

    @Schema(description = "Equipment", example = "Barbell")
    private String equipment;

    @Schema(description = "Number of sets", example = "3")
    private Integer sets;

    @Schema(description = "Number of reps", example = "10")
    private Integer reps;

    @Schema(description = "Weight", example = "60.5")
    private BigDecimal weight;

    @Schema(description = "Notes", example = "Focus on form")
    private String notes;

    @Schema(description = "Order in workout", example = "0")
    private Integer orderIndex;

    public static WorkoutExerciseResponse fromEntity(WorkoutExercise exercise) {
        return WorkoutExerciseResponse.builder()
                .id(exercise.getId())
                .exerciseId(exercise.getExerciseId())
                .exerciseName(exercise.getExerciseName())
                .muscleGroup(exercise.getMuscleGroup())
                .equipment(exercise.getEquipment())
                .sets(exercise.getSets())
                .reps(exercise.getReps())
                .weight(exercise.getWeight())
                .notes(exercise.getNotes())
                .orderIndex(exercise.getOrderIndex())
                .build();
    }
}
