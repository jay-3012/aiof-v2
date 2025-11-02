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
@Schema(description = "Weekly workout schedule")
public class WeeklyScheduleResponse {

    @Schema(description = "Day of week", example = "MONDAY")
    private String dayOfWeek;

    @Schema(description = "Workouts scheduled for this day")
    private List<WorkoutResponse> workouts;

    @Schema(description = "Number of workouts on this day", example = "2")
    private Integer workoutCount;
}
