package com.tech2nxt.aiofbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tech2nxt.aiofbackend.model.User;
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
@Schema(description = "User profile information")
public class UserResponse {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Full name", example = "John Doe")
    private String name;

    @Schema(description = "Phone number", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "Gender", example = "MALE")
    private String gender;

    @Schema(description = "Age", example = "25")
    private Integer age;

    @Schema(description = "Fitness goal", example = "Muscle Gain")
    private String fitnessGoal;

    @Schema(description = "Experience level", example = "Beginner")
    private String experienceLevel;

    @Schema(description = "Account creation date", example = "2025-11-01T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update date", example = "2025-11-01T10:30:00")
    private LocalDateTime updatedAt;

    /**
     * Convert User entity to UserResponse DTO
     */
    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender() != null ? user.getGender().name() : null)
                .age(user.getAge())
                .fitnessGoal(user.getFitnessGoal())
                .experienceLevel(user.getExperienceLevel())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
