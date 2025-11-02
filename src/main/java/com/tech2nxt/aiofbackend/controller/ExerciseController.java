package com.tech2nxt.aiofbackend.controller;

import com.tech2nxt.aiofbackend.dto.ApiResponse;
import com.tech2nxt.aiofbackend.dto.response.ExerciseCategoryResponse;
import com.tech2nxt.aiofbackend.dto.response.ExerciseResponse;
import com.tech2nxt.aiofbackend.dto.response.ExerciseSearchResponse;
import com.tech2nxt.aiofbackend.service.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Exercises", description = "Exercise library endpoints (powered by wger API)")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping
    @Operation(
            summary = "Search exercises",
            description = "Search and filter exercises from the wger database with pagination"
    )
    public ResponseEntity<ApiResponse<ExerciseSearchResponse>> searchExercises(
            @Parameter(description = "Search by exercise name")
            @RequestParam(required = false) String search,

            @Parameter(description = "Filter by category (e.g., 'Chest', 'Back')")
            @RequestParam(required = false) String category,

            @Parameter(description = "Filter by muscle group (e.g., 'Pectoralis', 'Biceps')")
            @RequestParam(required = false) String muscleGroup,

            @Parameter(description = "Filter by equipment (e.g., 'Barbell', 'Dumbbell')")
            @RequestParam(required = false) String equipment,

            @Parameter(description = "Page number (default: 1)")
            @RequestParam(required = false, defaultValue = "1") Integer page,

            @Parameter(description = "Page size (default: 20, max: 100)")
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {

        log.info("GET /api/exercises - search: {}, category: {}, page: {}",
                search, category, page);

        // Limit page size to prevent abuse
        if (pageSize > 100) {
            pageSize = 100;
        }

        ExerciseSearchResponse response = exerciseService.searchExercises(
                search, category, muscleGroup, equipment, page, pageSize);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get exercise by ID",
            description = "Retrieve detailed information about a specific exercise"
    )
    public ResponseEntity<ApiResponse<ExerciseResponse>> getExerciseById(
            @Parameter(description = "Exercise ID") @PathVariable String id) {

        log.info("GET /api/exercises/{}", id);

        ExerciseResponse response = exerciseService.getExerciseById(id);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/categories")
    @Operation(
            summary = "Get exercise categories",
            description = "Retrieve all available exercise categories/muscle groups"
    )
    public ResponseEntity<ApiResponse<List<ExerciseCategoryResponse>>> getCategories() {
        log.info("GET /api/exercises/categories");

        List<ExerciseCategoryResponse> response = exerciseService.getCategories();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/category/{categoryName}")
    @Operation(
            summary = "Get exercises by category",
            description = "Retrieve all exercises in a specific category"
    )
    public ResponseEntity<ApiResponse<List<ExerciseResponse>>> getExercisesByCategory(
            @Parameter(description = "Category name (e.g., 'Chest', 'Back')")
            @PathVariable String categoryName) {

        log.info("GET /api/exercises/category/{}", categoryName);

        List<ExerciseResponse> response = exerciseService.getExercisesByCategory(categoryName);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}