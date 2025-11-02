package com.tech2nxt.aiofbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tech2nxt.aiofbackend.model.JournalEntry;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Journal entry details")
public class JournalEntryResponse {

    @Schema(description = "Journal ID", example = "1")
    private Long id;

    @Schema(description = "Session ID", example = "1")
    private Long sessionId;

    @Schema(description = "Workout name", example = "Upper Body Day")
    private String workoutName;

    @Schema(description = "Image URL", example = "http://localhost:8080/uploads/1_1699012345_abc123.jpg")
    private String imageUrl;

    @Schema(description = "Journal description", example = "Great workout today!")
    private String description;

    @Schema(description = "Creation timestamp", example = "2025-11-02T15:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Workout date (from session)", example = "2025-11-02T14:00:00")
    private LocalDateTime workoutDate;

    @Schema(description = "Workout duration (from session)", example = "5400")
    private Integer workoutDuration;

    public static JournalEntryResponse fromEntity(JournalEntry journal, String baseUrl) {
        return JournalEntryResponse.builder()
                .id(journal.getId())
                .sessionId(journal.getSessionId())
                .imageUrl(journal.getFullImageUrl(baseUrl))
                .description(journal.getDescription())
                .createdAt(journal.getCreatedAt())
                .build();
    }

    public static JournalEntryResponse fromEntityMinimal(JournalEntry journal, String baseUrl) {
        return JournalEntryResponse.builder()
                .id(journal.getId())
                .sessionId(journal.getSessionId())
                .imageUrl(journal.getFullImageUrl(baseUrl))
                .createdAt(journal.getCreatedAt())
                .build();
    }
}