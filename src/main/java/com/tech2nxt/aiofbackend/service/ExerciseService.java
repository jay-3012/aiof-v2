package com.tech2nxt.aiofbackend.service;

import com.tech2nxt.aiofbackend.dto.external.WgerExercise;
import com.tech2nxt.aiofbackend.dto.external.WgerExerciseResponse;
import com.tech2nxt.aiofbackend.dto.response.ExerciseCategoryResponse;
import com.tech2nxt.aiofbackend.dto.response.ExerciseResponse;
import com.tech2nxt.aiofbackend.dto.response.ExerciseSearchResponse;
import com.tech2nxt.aiofbackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseApiClient exerciseApiClient;

    /**
     * Search exercises with pagination and filtering
     */
    public ExerciseSearchResponse searchExercises(
            String search,
            String category,
            String muscleGroup,
            String equipment,
            Integer page,
            Integer pageSize) {

        log.info("Searching exercises - search: {}, category: {}, muscle: {}, equipment: {}, page: {}",
                search, category, muscleGroup, equipment, page);

        // Fetch from wger API
        WgerExerciseResponse wgerResponse = exerciseApiClient.fetchExercises(page, pageSize, search);

        if (wgerResponse == null || wgerResponse.getResults() == null) {
            return ExerciseSearchResponse.builder()
                    .totalCount(0)
                    .page(page != null ? page : 1)
                    .pageSize(pageSize != null ? pageSize : 20)
                    .totalPages(0)
                    .exercises(new ArrayList<>())
                    .build();
        }

        // Convert to response DTOs
        List<ExerciseResponse> exercises = wgerResponse.getResults().stream()
                .map(this::mapToExerciseResponse)
                .collect(Collectors.toList());

        // Apply filters (category, muscle, equipment)
        if (category != null && !category.isEmpty()) {
            exercises = exercises.stream()
                    .filter(ex -> category.equalsIgnoreCase(ex.getCategory()))
                    .collect(Collectors.toList());
        }

        if (muscleGroup != null && !muscleGroup.isEmpty()) {
            exercises = exercises.stream()
                    .filter(ex -> ex.getPrimaryMuscles() != null &&
                            ex.getPrimaryMuscles().stream()
                                    .anyMatch(m -> m.toLowerCase().contains(muscleGroup.toLowerCase())))
                    .collect(Collectors.toList());
        }

        if (equipment != null && !equipment.isEmpty()) {
            exercises = exercises.stream()
                    .filter(ex -> ex.getEquipment() != null &&
                            ex.getEquipment().stream()
                                    .anyMatch(e -> e.toLowerCase().contains(equipment.toLowerCase())))
                    .collect(Collectors.toList());
        }

        // Calculate pagination
        int currentPage = page != null ? page : 1;
        int currentPageSize = pageSize != null ? pageSize : 20;
        int totalCount = wgerResponse.getCount();
        int totalPages = (int) Math.ceil((double) totalCount / currentPageSize);

        return ExerciseSearchResponse.builder()
                .totalCount(totalCount)
                .page(currentPage)
                .pageSize(currentPageSize)
                .totalPages(totalPages)
                .exercises(exercises)
                .nextPage(wgerResponse.getNext())
                .previousPage(wgerResponse.getPrevious())
                .build();
    }

    /**
     * Get exercise by ID (with caching)
     */
    @org.springframework.cache.annotation.Cacheable(value = "exerciseDetails", key = "#exerciseId")
    public ExerciseResponse getExerciseById(String exerciseId) {
        log.info("Fetching exercise by ID: {} (cache miss)", exerciseId);

        try {
            Integer id = Integer.parseInt(exerciseId);
            WgerExercise wgerExercise = exerciseApiClient.fetchExerciseById(id);

            if (wgerExercise == null) {
                throw new ResourceNotFoundException("Exercise", "id", exerciseId);
            }

            return mapToExerciseResponse(wgerExercise);

        } catch (NumberFormatException e) {
            throw new ResourceNotFoundException("Exercise", "id", exerciseId);
        }
    }

    /**
     * Get all exercise categories
     */
    public List<ExerciseCategoryResponse> getCategories() {
        log.info("Fetching exercise categories");

        Map<Integer, String> categories = exerciseApiClient.fetchCategories();

        return categories.entrySet().stream()
                .map(entry -> ExerciseCategoryResponse.builder()
                        .id(entry.getKey().toString())
                        .name(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Get exercises by category
     */
    public List<ExerciseResponse> getExercisesByCategory(String categoryName) {
        log.info("Fetching exercises for category: {}", categoryName);

        // Search all exercises and filter by category
        ExerciseSearchResponse searchResponse = searchExercises(null, categoryName, null, null, 1, 100);
        return searchResponse.getExercises();
    }

    /**
     * Map wger exercise to response DTO
     */
    private ExerciseResponse mapToExerciseResponse(WgerExercise wgerExercise) {
        // Get category name
        String categoryName = wgerExercise.getCategory() != null
                ? exerciseApiClient.getCategoryName(wgerExercise.getCategory())
                : "Unknown";

        // Get primary muscles
        List<String> primaryMuscles = wgerExercise.getMuscles() != null
                ? wgerExercise.getMuscles().stream()
                .map(exerciseApiClient::getMuscleName)
                .collect(Collectors.toList())
                : new ArrayList<>();

        // Get secondary muscles
        List<String> secondaryMuscles = wgerExercise.getMusclesSecondary() != null
                ? wgerExercise.getMusclesSecondary().stream()
                .map(exerciseApiClient::getMuscleName)
                .collect(Collectors.toList())
                : new ArrayList<>();

        // Get equipment
        List<String> equipment = wgerExercise.getEquipment() != null
                ? wgerExercise.getEquipment().stream()
                .map(exerciseApiClient::getEquipmentName)
                .collect(Collectors.toList())
                : new ArrayList<>();

        // Get variations
        List<String> variations = wgerExercise.getVariations() != null
                ? wgerExercise.getVariations().stream()
                .map(String::valueOf)
                .collect(Collectors.toList())
                : new ArrayList<>();

        return ExerciseResponse.builder()
                .id(wgerExercise.getId().toString())
                .name(wgerExercise.getName())
                .description(wgerExercise.getDescription())
                .category(categoryName)
                .primaryMuscles(primaryMuscles)
                .secondaryMuscles(secondaryMuscles)
                .equipment(equipment)
                .variations(variations)
                .build();
    }
}