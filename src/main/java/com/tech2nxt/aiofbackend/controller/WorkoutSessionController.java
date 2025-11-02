package com.tech2nxt.aiofbackend.controller;

import com.tech2nxt.aiofbackend.dto.ApiResponse;
import com.tech2nxt.aiofbackend.dto.request.CompleteSessionRequest;
import com.tech2nxt.aiofbackend.dto.request.LogExerciseRequest;
import com.tech2nxt.aiofbackend.dto.request.StartSessionRequest;
import com.tech2nxt.aiofbackend.dto.response.SessionHistoryResponse;
import com.tech2nxt.aiofbackend.dto.response.WorkoutSessionResponse;
import com.tech2nxt.aiofbackend.security.UserPrincipal;
import com.tech2nxt.aiofbackend.service.WorkoutSessionService;
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

@Slf4j
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Workout Sessions", description = "Track active workout executions")
public class WorkoutSessionController {

    private final WorkoutSessionService sessionService;

    @PostMapping
    @Operation(
            summary = "Start workout session",
            description = "Begin a new workout session with timer"
    )
    public ResponseEntity<ApiResponse<WorkoutSessionResponse>> startSession(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody StartSessionRequest request) {
        log.info("POST /api/sessions - User ID: {}", userPrincipal.getId());

        WorkoutSessionResponse response = sessionService.startSession(userPrincipal.getId(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Workout session started", response));
    }

    @GetMapping("/active")
    @Operation(
            summary = "Get active session",
            description = "Retrieve the current active workout session if exists"
    )
    public ResponseEntity<ApiResponse<WorkoutSessionResponse>> getActiveSession(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("GET /api/sessions/active - User ID: {}", userPrincipal.getId());

        WorkoutSessionResponse response = sessionService.getActiveSession(userPrincipal.getId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/exercises")
    @Operation(
            summary = "Log exercise",
            description = "Add an exercise log to the active session"
    )
    public ResponseEntity<ApiResponse<WorkoutSessionResponse>> logExercise(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Session ID") @PathVariable Long id,
            @Valid @RequestBody LogExerciseRequest request) {
        log.info("POST /api/sessions/{}/exercises - User ID: {}", id, userPrincipal.getId());

        WorkoutSessionResponse response = sessionService.logExercise(id, userPrincipal.getId(), request);

        return ResponseEntity.ok(ApiResponse.success("Exercise logged successfully", response));
    }

    @PutMapping("/{id}/complete")
    @Operation(
            summary = "Complete session",
            description = "Mark session as completed (ready for journal entry)"
    )
    public ResponseEntity<ApiResponse<WorkoutSessionResponse>> completeSession(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Session ID") @PathVariable Long id,
            @Valid @RequestBody(required = false) CompleteSessionRequest request) {
        log.info("PUT /api/sessions/{}/complete - User ID: {}", id, userPrincipal.getId());

        WorkoutSessionResponse response = sessionService.completeSession(
                id,
                userPrincipal.getId(),
                request != null ? request : new CompleteSessionRequest()
        );

        return ResponseEntity.ok(ApiResponse.success("Workout session completed", response));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get session by ID",
            description = "Retrieve detailed session information including all exercise logs"
    )
    public ResponseEntity<ApiResponse<WorkoutSessionResponse>> getSessionById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Session ID") @PathVariable Long id) {
        log.info("GET /api/sessions/{} - User ID: {}", id, userPrincipal.getId());

        WorkoutSessionResponse response = sessionService.getSessionById(id, userPrincipal.getId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/history")
    @Operation(
            summary = "Get workout history",
            description = "Retrieve paginated list of past workout sessions"
    )
    public ResponseEntity<ApiResponse<SessionHistoryResponse>> getSessionHistory(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("GET /api/sessions/history - User ID: {}, page: {}", userPrincipal.getId(), page);

        SessionHistoryResponse response = sessionService.getSessionHistory(
                userPrincipal.getId(), page, pageSize);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Abandon session",
            description = "Mark an active session as abandoned"
    )
    public ResponseEntity<ApiResponse<Void>> abandonSession(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Session ID") @PathVariable Long id) {
        log.info("DELETE /api/sessions/{} - User ID: {}", id, userPrincipal.getId());

        sessionService.abandonSession(id, userPrincipal.getId());

        return ResponseEntity.ok(ApiResponse.success("Session abandoned successfully"));
    }
}