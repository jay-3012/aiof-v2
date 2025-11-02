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
@Schema(description = "Paginated session history")
public class SessionHistoryResponse {

    @Schema(description = "List of sessions")
    private List<WorkoutSessionResponse> sessions;

    @Schema(description = "Current page", example = "0")
    private Integer currentPage;

    @Schema(description = "Page size", example = "20")
    private Integer pageSize;

    @Schema(description = "Total pages", example = "5")
    private Integer totalPages;

    @Schema(description = "Total sessions", example = "95")
    private Long totalSessions;

    @Schema(description = "Has next page", example = "true")
    private Boolean hasNext;

    @Schema(description = "Has previous page", example = "false")
    private Boolean hasPrevious;
}
