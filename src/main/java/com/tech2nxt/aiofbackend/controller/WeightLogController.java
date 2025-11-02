package com.tech2nxt.aiofbackend.controller;

import com.tech2nxt.aiofbackend.dto.ApiResponse;
import com.tech2nxt.aiofbackend.dto.request.LogWeightRequest;
import com.tech2nxt.aiofbackend.dto.response.WeightHistoryResponse;
import com.tech2nxt.aiofbackend.dto.response.WeightLogResponse;
import com.tech2nxt.aiofbackend.security.UserPrincipal;
import com.tech2nxt.aiofbackend.service.WeightLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/weight")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Weight Tracking", description = "Body weight logging and history")
public class WeightLogController {

    private final WeightLogService weightLogService;

    @PostMapping
    @Operation(
            summary = "Log body weight",
            description = "Record body weight for a specific date"
    )
    public ResponseEntity<ApiResponse<WeightLogResponse>> logWeight(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody LogWeightRequest request) {

        log.info("POST /api/weight - User ID: {}", userPrincipal.getId());

        WeightLogResponse response = weightLogService.logWeight(userPrincipal.getId(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Weight logged successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get weight log by ID",
            description = "Retrieve a specific weight log entry"
    )
    public ResponseEntity<ApiResponse<WeightLogResponse>> getWeightLogById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Weight log ID") @PathVariable Long id) {

        log.info("GET /api/weight/{} - User ID: {}", id, userPrincipal.getId());

        WeightLogResponse response = weightLogService.getWeightLogById(id, userPrincipal.getId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/history")
    @Operation(
            summary = "Get weight history",
            description = "Retrieve weight logs with statistics for a date range"
    )
    public ResponseEntity<ApiResponse<WeightHistoryResponse>> getWeightHistory(
            @AuthenticationPrincipal UserPrincipal userPrincipal,

            @Parameter(description = "Start date (optional)", example = "2025-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "End date (optional)", example = "2025-11-02")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/weight/history - User ID: {}", userPrincipal.getId());

        WeightHistoryResponse response = weightLogService.getWeightHistory(
                userPrincipal.getId(), startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update weight log",
            description = "Update an existing weight log entry"
    )
    public ResponseEntity<ApiResponse<WeightLogResponse>> updateWeightLog(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Weight log ID") @PathVariable Long id,
            @Valid @RequestBody LogWeightRequest request) {

        log.info("PUT /api/weight/{} - User ID: {}", id, userPrincipal.getId());

        WeightLogResponse response = weightLogService.updateWeightLog(
                id, userPrincipal.getId(), request);

        return ResponseEntity.ok(ApiResponse.success("Weight log updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete weight log",
            description = "Delete a weight log entry"
    )
    public ResponseEntity<ApiResponse<Void>> deleteWeightLog(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Weight log ID") @PathVariable Long id) {

        log.info("DELETE /api/weight/{} - User ID: {}", id, userPrincipal.getId());

        weightLogService.deleteWeightLog(id, userPrincipal.getId());

        return ResponseEntity.ok(ApiResponse.success("Weight log deleted successfully"));
    }
}