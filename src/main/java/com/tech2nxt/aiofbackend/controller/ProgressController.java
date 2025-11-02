package com.tech2nxt.aiofbackend.controller;

import com.tech2nxt.aiofbackend.dto.ApiResponse;
import com.tech2nxt.aiofbackend.dto.response.ProgressStatsResponse;
import com.tech2nxt.aiofbackend.dto.response.StreakResponse;
import com.tech2nxt.aiofbackend.security.UserPrincipal;
import com.tech2nxt.aiofbackend.service.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Progress", description = "Progress tracking and statistics")
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping("/stats")
    @Operation(
            summary = "Get progress statistics",
            description = "Retrieve overall progress stats including workouts, streaks, and weight"
    )
    public ResponseEntity<ApiResponse<ProgressStatsResponse>> getProgressStats(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/progress/stats - User ID: {}", userPrincipal.getId());

        ProgressStatsResponse response = progressService.getProgressStats(userPrincipal.getId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/streak")
    @Operation(
            summary = "Get workout streak",
            description = "Retrieve current workout streak and related information"
    )
    public ResponseEntity<ApiResponse<StreakResponse>> getStreak(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/progress/streak - User ID: {}", userPrincipal.getId());

        StreakResponse response = progressService.calculateStreak(userPrincipal.getId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}