package com.tech2nxt.aiofbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tech2nxt.aiofbackend.model.ExerciseLog;
import com.tech2nxt.aiofbackend.model.WorkoutSession;
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
@Schema(description = "Exercise performance log")
public class ExerciseLogResponse {

    @Schema(description = "Log ID", example = "1")
    private Long id;

    @Schema(description = "Exercise ID", example = "228")
    private String exerciseId;

    @Schema(description = "Exercise name", example = "Bench Press")
    private String exerciseName;

    @Schema(description = "Muscle group", example = "Chest")
    private String muscleGroup;

    @Schema(description = "Equipment", example = "Barbell")
    private String equipment;

    @Schema(description = "Sets completed", example = "4")
    private Integer sets;

    @Schema(description = "Reps per set", example = "10")
    private Integer reps;

    @Schema(description = "Weight used", example = "60.5")
    private BigDecimal weight;

    @Schema(description = "Duration in seconds", example = "180")
    private Integer durationSeconds;

    @Schema(description = "Notes", example = "Felt strong")
    private String notes;

    @Schema(description = "Order in workout", example = "0")
    private Integer orderIndex;

    public static ExerciseLogResponse fromEntity(ExerciseLog log) {
        return ExerciseLogResponse.builder()
                .id(log.getId())
                .exerciseId(log.getExerciseId())
                .exerciseName(log.getExerciseName())
                .muscleGroup(log.getMuscleGroup())
                .equipment(log.getEquipment())
                .sets(log.getSets())
                .reps(log.getReps())
                .weight(log.getWeight())
                .durationSeconds(log.getDurationSeconds())
                .notes(log.getNotes())
                .orderIndex(log.getOrderIndex())
                .build();
    }
}