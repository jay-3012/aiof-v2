package com.tech2nxt.aiofbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Start a new workout session")
public class StartSessionRequest {

    @Schema(description = "Workout template ID (optional - can be custom session)", example = "1")
    private Long workoutId;

    @Schema(description = "Custom workout name (required if workoutId is null)", example = "Quick Morning Workout")
    private String workoutName;
}
