package com.tech2nxt.aiofbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create journal entry after workout")
public class CreateJournalRequest {

    @NotNull(message = "Session ID is required")
    @Schema(description = "Workout session ID", example = "1", required = true)
    private Long sessionId;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
    @Schema(description = "Journal entry text",
            example = "Great workout today! Felt strong on bench press. Need to work on form for squats.",
            required = true)
    private String description;
}