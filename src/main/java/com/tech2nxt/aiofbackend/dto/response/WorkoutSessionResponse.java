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
@Schema(description = "Workout session details")
public class WorkoutSessionResponse {

    @Schema(description = "Session ID", example = "1")
    private Long id;

    @Schema(description = "Workout template ID", example = "1")
    private Long workoutId;

    @Schema(description = "Workout name", example = "Upper Body Day")
    private String workoutName;

    @Schema(description = "Session status", example = "IN_PROGRESS")
    private String status;

    @Schema(description = "Start time", example = "2025-11-02T10:00:00")
    private LocalDateTime startTime;

    @Schema(description = "End time", example = "2025-11-02T11:30:00")
    private LocalDateTime endTime;

    @Schema(description = "Total duration in seconds", example = "5400")
    private Integer totalDurationSeconds;

    @Schema(description = "Total duration formatted (HH:mm:ss)", example = "01:30:00")
    private String durationFormatted;

    @Schema(description = "Exercises performed")
    private List<ExerciseLogResponse> exercises;

    @Schema(description = "Number of exercises", example = "5")
    private Integer exerciseCount;

    @Schema(description = "Has journal entry", example = "false")
    private Boolean hasJournal;

    public static WorkoutSessionResponse fromEntity(WorkoutSession session) {
        return WorkoutSessionResponse.builder()
                .id(session.getId())
                .workoutId(session.getWorkoutId())
                .workoutName(session.getWorkoutName())
                .status(session.getStatus().name())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .totalDurationSeconds(session.getTotalDurationSeconds())
                .durationFormatted(formatDuration(session.getTotalDurationSeconds()))
                .exercises(session.getExerciseLogs().stream()
                        .map(ExerciseLogResponse::fromEntity)
                        .collect(Collectors.toList()))
                .exerciseCount(session.getExerciseLogs().size())
                .hasJournal(false) // Will be updated in Phase 5
                .build();
    }

    public static WorkoutSessionResponse fromEntityMinimal(WorkoutSession session) {
        return WorkoutSessionResponse.builder()
                .id(session.getId())
                .workoutId(session.getWorkoutId())
                .workoutName(session.getWorkoutName())
                .status(session.getStatus().name())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .totalDurationSeconds(session.getTotalDurationSeconds())
                .durationFormatted(formatDuration(session.getTotalDurationSeconds()))
                .exerciseCount(session.getExerciseLogs().size())
                .hasJournal(false) // Will be updated in Phase 5
                .build();
    }

    private static String formatDuration(Integer seconds) {
        if (seconds == null) return null;
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
}