package com.tech2nxt.aiofbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Weight tracking history with stats")
public class WeightHistoryResponse {

    @Schema(description = "List of weight logs")
    private List<WeightLogResponse> logs;

    @Schema(description = "Current weight", example = "75.5")
    private BigDecimal currentWeight;

    @Schema(description = "Starting weight", example = "80.0")
    private BigDecimal startingWeight;

    @Schema(description = "Total weight change", example = "-4.5")
    private BigDecimal totalChange;

    @Schema(description = "Average weight", example = "77.2")
    private BigDecimal averageWeight;

    @Schema(description = "Lowest weight", example = "75.0")
    private BigDecimal lowestWeight;

    @Schema(description = "Highest weight", example = "80.5")
    private BigDecimal highestWeight;
}
