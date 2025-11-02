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
@Schema(description = "Complete an active workout session")
public class CompleteSessionRequest {

    @Valid
    @Schema(description = "Final list of all exercises performed (optional - can use logged exercises)")
    private List<LogExerciseRequest> exercises;

    @Schema(description = "Additional notes about the session", example = "Great workout!")
    private String notes;
}
