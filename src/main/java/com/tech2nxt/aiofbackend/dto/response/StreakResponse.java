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
@Schema(description = "Workout streak details")
public class StreakResponse {

    @Schema(description = "Current streak (consecutive days)", example = "7")
    private Integer currentStreak;

    @Schema(description = "Longest streak ever", example = "14")
    private Integer longestStreak;

    @Schema(description = "Streak start date", example = "2025-10-27")
    private LocalDate streakStartDate;

    @Schema(description = "Is streak active today", example = "true")
    private Boolean isActiveToday;

    @Schema(description = "Days until next workout needed", example = "0")
    private Integer daysUntilBreak;

    @Schema(description = "Streak status message", example = "Great job! Keep it going!")
    private String message;
}

