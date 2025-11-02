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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Weight log entry")
public class WeightLogResponse {

    @Schema(description = "Weight log ID", example = "1")
    private Long id;

    @Schema(description = "Weight in kg or lbs", example = "75.5")
    private BigDecimal weight;

    @Schema(description = "Log date", example = "2025-11-02")
    private LocalDate logDate;

    @Schema(description = "Notes", example = "Feeling good!")
    private String notes;

    @Schema(description = "Weight change from previous entry", example = "-0.5")
    private BigDecimal weightChange;

    @Schema(description = "Created timestamp", example = "2025-11-02T10:00:00")
    private LocalDateTime createdAt;

    public static WeightLogResponse fromEntity(WeightLog log) {
        return WeightLogResponse.builder()
                .id(log.getId())
                .weight(log.getWeight())
                .logDate(log.getLogDate())
                .notes(log.getNotes())
                .createdAt(log.getCreatedAt())
                .build();
    }

    public static WeightLogResponse fromEntity(WeightLog log, BigDecimal previousWeight) {
        WeightLogResponse response = fromEntity(log);
        if (previousWeight != null) {
            response.setWeightChange(log.getWeight().subtract(previousWeight));
        }
        return response;
    }
}
