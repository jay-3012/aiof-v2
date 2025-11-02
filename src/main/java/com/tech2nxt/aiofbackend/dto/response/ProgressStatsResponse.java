package com.tech2nxt.aiofbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tech2nxt.aiofbackend.model.WeightLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Overall progress statistics")

public class ProgressStatsResponse {

    @Schema(description = "Total completed workouts", example = "45")
    private Long totalWorkouts;

    @Schema(description = "Current workout streak (consecutive days)", example = "7")
    private Integer currentStreak;

    @Schema(description = "Longest workout streak", example = "14")
    private Integer longestStreak;

    @Schema(description = "Total workout time in seconds", example = "54000")
    private Long totalWorkoutTimeSeconds;

    @Schema(description = "Total workout time formatted (HH:mm:ss)", example = "15:00:00")
    private String totalWorkoutTimeFormatted;

    @Schema(description = "Average workout duration in seconds", example = "3600")
    private Integer avgWorkoutDuration;

    @Schema(description = "Average workout duration formatted (HH:mm:ss)", example = "01:00:00")
    private String avgWorkoutDurationFormatted;

    @Schema(description = "Workouts this week", example = "3")
    private Integer workoutsThisWeek;

    @Schema(description = "Workouts this month", example = "12")
    private Integer workoutsThisMonth;

    @Schema(description = "Total journals written", example = "45")
    private Long totalJournals;

    @Schema(description = "Current weight", example = "75.5")
    private BigDecimal currentWeight;

    @Schema(description = "Starting weight", example = "80.0")
    private BigDecimal startingWeight;

    @Schema(description = "Weight change", example = "-4.5")
    private BigDecimal weightChange;

    @Schema(description = "Last workout date", example = "2025-11-02T14:00:00")
    private LocalDateTime lastWorkoutDate;
}
