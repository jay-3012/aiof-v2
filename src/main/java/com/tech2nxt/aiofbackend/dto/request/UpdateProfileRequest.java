package com.tech2nxt.aiofbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Update user profile request")
public class UpdateProfileRequest {

    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    @Schema(description = "User full name", example = "John Doe")
    private String name;

    @Schema(description = "Phone number", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "Gender", example = "MALE", allowableValues = {"MALE", "FEMALE", "OTHER"})
    private String gender;

    @Schema(description = "Age", example = "25")
    private Integer age;

    @Schema(description = "Fitness goal", example = "Muscle Gain")
    private String fitnessGoal;

    @Schema(description = "Experience level", example = "Intermediate", allowableValues = {"Beginner", "Intermediate", "Advanced"})
    private String experienceLevel;
}
