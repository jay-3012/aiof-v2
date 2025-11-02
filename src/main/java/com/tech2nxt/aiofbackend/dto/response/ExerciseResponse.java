package com.tech2nxt.aiofbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Exercise details")
public class ExerciseResponse {

    @Schema(description = "Exercise ID", example = "123")
    private String id;

    @Schema(description = "Exercise name", example = "Bench Press")
    private String name;

    @Schema(description = "Exercise description (HTML format)")
    private String description;

    @Schema(description = "Category/muscle group", example = "Chest")
    private String category;

    @Schema(description = "Primary muscles targeted", example = "[\"Pectoralis major\"]")
    private List<String> primaryMuscles;

    @Schema(description = "Secondary muscles targeted", example = "[\"Triceps\"]")
    private List<String> secondaryMuscles;

    @Schema(description = "Equipment needed", example = "[\"Barbell\", \"Bench\"]")
    private List<String> equipment;

    @Schema(description = "Exercise variations", example = "[\"124\", \"125\"]")
    private List<String> variations;

    @Schema(description = "Difficulty level", example = "Intermediate")
    private String difficulty;
}

