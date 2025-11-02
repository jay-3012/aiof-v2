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
@Schema(description = "Exercise search results with pagination")
public class ExerciseSearchResponse {

    @Schema(description = "Total number of exercises", example = "150")
    private Integer totalCount;

    @Schema(description = "Current page number", example = "1")
    private Integer page;

    @Schema(description = "Page size", example = "20")
    private Integer pageSize;

    @Schema(description = "Total pages", example = "8")
    private Integer totalPages;

    @Schema(description = "List of exercises")
    private List<ExerciseResponse> exercises;

    @Schema(description = "Next page URL")
    private String nextPage;

    @Schema(description = "Previous page URL")
    private String previousPage;
}
