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
@Schema(description = "Exercise category/muscle group")
public class ExerciseCategoryResponse {

    @Schema(description = "Category ID", example = "10")
    private String id;

    @Schema(description = "Category name", example = "Chest")
    private String name;

    @Schema(description = "Number of exercises in category", example = "25")
    private Integer exerciseCount;
}
