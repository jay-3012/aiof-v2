//package com.tech2nxt.aiofbackend.controller;
//
//import com.tech2nxt.aiofbackend.dto.ApiResponse;
//import com.tech2nxt.aiofbackend.dto.request.CreateWeightGoalRequest;
//import com.tech2nxt.aiofbackend.dto.response.WeightGoalResponse;
//import com.tech2nxt.aiofbackend.security.UserPrincipal;
//import com.tech2nxt.aiofbackend.service.WeightGoalService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/weight/goals")
//@RequiredArgsConstructor
//@SecurityRequirement(name = "bearerAuth")
//@Tag(name = "Weight Goals", description = "Weight goal setting and tracking")
//public class WeightGoalController {
//
//    private final WeightGoalService goalService;
//
//    @PostMapping
//    @Operation(
//            summary = "Create weight goal",
//            description = "Set a new weight goal with target weight and date"
//    )
//    public ResponseEntity<ApiResponse<WeightGoalResponse>> createGoal(
//            @AuthenticationPrincipal UserPrincipal userPrincipal,
//            @Valid @RequestBody CreateWeightGoalRequest request) {
//
//        log.info("POST /api/weight/goals - User ID: {}", userPrincipal.getId());
//
//        WeightGoalResponse response = goalService.createWeightGoal(userPrincipal.getId(), request);
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(ApiResponse.success("Weight goal created successfully", response));
//    }
//
//    @GetMapping("/active")
//    @Operation(
//            summary = "Get active weight goal",
//            description = "Retrieve the current active weight goal"
//    )
//    public ResponseEntity<ApiResponse<WeightGoalResponse>> getActiveGoal(
//            @AuthenticationPrincipal UserPrincipal userPrincipal) {
//
//        log.info("GET /api/weight/goals/active - User ID: {}", userPrincipal.getId());
//
//        WeightGoalResponse response = goalService.getActiveGoal(userPrincipal.getId());
//
//        return ResponseEntity.ok(ApiResponse.success(response));
//    }
//
//    @GetMapping("/{id}")
//    @Operation(
//            summary = "Get weight goal by ID",
//            description = "Retrieve a specific weight goal"
//    )
//    public ResponseEntity<ApiResponse<WeightGoalResponse>> getGoalById(
//            @AuthenticationPrincipal UserPrincipal userPrincipal,
//            @Parameter(description = "Goal ID") @PathVariable Long id) {
//
//        log.info("GET /api/weight/goals/{} - User ID: {}", id, userPrincipal.getId());
//
//        WeightGoalResponse response = goalService.getWeightGoalById(id, userPrincipal.getId());
//
//        return ResponseEntity.ok(ApiResponse.success(response));
//    }
//
//    @GetMapping
//    @Operation(
//            summary = "Get all weight goals",
//            description = "Retrieve all weight goals (active and completed)"
//    )
//    public ResponseEntity<ApiResponse<List<WeightGoalResponse>>> getAllGoals(
//            @AuthenticationPrincipal UserPrincipal userPrincipal) {
//
//        log.info("GET /api/weight/goals - User ID: {}", userPrincipal.getId());
//
//        List<WeightGoalResponse> response = goalService.getAllGoals(userPrincipal.getId());
//
//        return ResponseEntity.ok(ApiResponse.success(response));
//    }
//
//    @PutMapping("/{id}")
//    @Operation(
//            summary = "Update weight goal",
//            description = "Update target weight, date, or notes"
//    )
//    public ResponseEntity<ApiResponse<WeightGoalResponse>> updateGoal(
//            @AuthenticationPrincipal UserPrincipal userPrincipal,
//            @Parameter(description = "Goal ID") @PathVariable Long id,
//            @Valid @RequestBody CreateWeightGoalRequest request) {
//
//        log.info("PUT /api/weight/goals/{} - User ID: {}", id, userPrincipal.getId());
//
//        WeightGoalResponse response = goalService.updateWeightGoal(
//                id, userPrincipal.getId(), request);
//
//        return ResponseEntity.ok(ApiResponse.success("Weight goal updated successfully", response));
//    }
//
//    @PutMapping("/{id}/complete")
//    @Operation(
//            summary = "Complete weight goal",
//            description = "Mark a weight goal as completed"
//    )
//    public ResponseEntity<ApiResponse<WeightGoalResponse>> completeGoal(
//            @AuthenticationPrincipal UserPrincipal userPrincipal,
//            @Parameter(description = "Goal ID") @PathVariable Long id) {
//
//        log.info("PUT /api/weight/goals/{}/complete - User ID: {}", id, userPrincipal.getId());
//
//        WeightGoalResponse response = goalService.completeGoal(id, userPrincipal.getId());
//
//        return ResponseEntity.ok(ApiResponse.success("Weight goal completed!", response));
//    }
//
//    @PutMapping("/{id}/abandon")
//    @Operation(
//            summary = "Abandon weight goal",
//            description = "Mark a weight goal as abandoned"
//    )
//    public ResponseEntity<ApiResponse<Void>> abandonGoal(
//            @AuthenticationPrincipal UserPrincipal userPrincipal,
//            @Parameter(description = "Goal ID") @PathVariable Long id) {
//
//        log.info("PUT /api/weight/goals/{}/abandon - User ID: {}", id, userPrincipal.getId());
//
//        goalService.abandonGoal(id, userPrincipal.getId());
//
//        return ResponseEntity.ok(ApiResponse.success("Weight goal abandoned"));
//    }
//
//    @DeleteMapping("/{id}")
//    @Operation(
//            summary = "Delete weight goal",
//            description = "Delete a weight goal"
//    )
//    public ResponseEntity<ApiResponse<Void>> deleteGoal(
//            @AuthenticationPrincipal UserPrincipal userPrincipal,
//            @Parameter(description = "Goal ID") @PathVariable Long id) {
//
//        log.info("DELETE /api/weight/goals/{} - User ID: {}", id, userPrincipal.getId());
//
//        goalService.deleteGoal(id, userPrincipal.getId());
//
//        return ResponseEntity.ok(ApiResponse.success("Weight goal deleted successfully"));
//    }
//}