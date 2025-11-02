package com.tech2nxt.aiofbackend.controller;

import com.tech2nxt.aiofbackend.dto.ApiResponse;
import com.tech2nxt.aiofbackend.dto.request.CreateWorkoutRequest;
import com.tech2nxt.aiofbackend.dto.request.UpdateWorkoutRequest;
import com.tech2nxt.aiofbackend.dto.response.WeeklyScheduleResponse;
import com.tech2nxt.aiofbackend.dto.response.WorkoutResponse;
import com.tech2nxt.aiofbackend.security.UserPrincipal;
import com.tech2nxt.aiofbackend.service.WorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Workouts", description = "Workout management endpoints")
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping
    @Operation(
            summary = "Create workout",
            description = "Create a new workout with exercises and optional scheduling"
    )
    public ResponseEntity<ApiResponse<WorkoutResponse>> createWorkout(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CreateWorkoutRequest request) {
        log.info("POST /api/workouts - User ID: {}", userPrincipal.getId());

        WorkoutResponse response = workoutService.createWorkout(userPrincipal.getId(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Workout created successfully", response));
    }

    @GetMapping
    @Operation(
            summary = "Get all workouts",
            description = "Retrieve all workouts for the authenticated user"
    )
    public ResponseEntity<ApiResponse<List<WorkoutResponse>>> getAllWorkouts(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("GET /api/workouts - User ID: {}", userPrincipal.getId());

        List<WorkoutResponse> response = workoutService.getAllWorkouts(userPrincipal.getId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get workout by ID",
            description = "Retrieve a specific workout with all exercises and schedule"
    )
    public ResponseEntity<ApiResponse<WorkoutResponse>> getWorkoutById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Workout ID") @PathVariable Long id) {
        log.info("GET /api/workouts/{} - User ID: {}", id, userPrincipal.getId());

        WorkoutResponse response = workoutService.getWorkoutById(id, userPrincipal.getId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update workout",
            description = "Update an existing workout (name, description, exercises, or schedule)"
    )
    public ResponseEntity<ApiResponse<WorkoutResponse>> updateWorkout(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Workout ID") @PathVariable Long id,
            @Valid @RequestBody UpdateWorkoutRequest request) {
        log.info("PUT /api/workouts/{} - User ID: {}", id, userPrincipal.getId());

        WorkoutResponse response = workoutService.updateWorkout(id, userPrincipal.getId(), request);

        return ResponseEntity.ok(ApiResponse.success("Workout updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete workout",
            description = "Delete a workout and all associated exercises and schedules"
    )
    public ResponseEntity<ApiResponse<Void>> deleteWorkout(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Workout ID") @PathVariable Long id) {
        log.info("DELETE /api/workouts/{} - User ID: {}", id, userPrincipal.getId());

        workoutService.deleteWorkout(id, userPrincipal.getId());

        return ResponseEntity.ok(ApiResponse.success("Workout deleted successfully"));
    }

    @GetMapping("/schedule")
    @Operation(
            summary = "Get weekly schedule",
            description = "Retrieve all workouts organized by day of week"
    )
    public ResponseEntity<ApiResponse<List<WeeklyScheduleResponse>>> getWeeklySchedule(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("GET /api/workouts/schedule - User ID: {}", userPrincipal.getId());

        List<WeeklyScheduleResponse> response = workoutService.getWeeklySchedule(userPrincipal.getId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/schedule/{day}")
    @Operation(
            summary = "Get workouts for specific day",
            description = "Retrieve all workouts scheduled for a specific day of the week"
    )
    public ResponseEntity<ApiResponse<List<WorkoutResponse>>> getWorkoutsByDay(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Day of week (e.g., MONDAY, TUESDAY)", example = "MONDAY")
            @PathVariable String day) {
        log.info("GET /api/workouts/schedule/{} - User ID: {}", day, userPrincipal.getId());

        List<WorkoutResponse> response = workoutService.getWorkoutsByDay(userPrincipal.getId(), day);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}