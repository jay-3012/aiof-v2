package com.tech2nxt.aiofbackend.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech2nxt.aiofbackend.dto.external.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ExerciseApiClient {

    @Value("${app.wger.api.base-url}")
    private String baseUrl;

    @Value("${app.wger.api.language}")
    private Integer language; // 2 = English

    private final RestTemplate restTemplate;
    private final Map<Integer, String> categoryCache = new HashMap<>();
    private final Map<Integer, String> muscleCache = new HashMap<>();
    private final Map<Integer, String> equipmentCache = new HashMap<>();

    public ExerciseApiClient() {
        // Create custom ObjectMapper for wger API
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // Create message converter with custom ObjectMapper
        MappingJackson2HttpMessageConverter messageConverter =
                new MappingJackson2HttpMessageConverter(objectMapper);

        // Configure RestTemplate
        this.restTemplate = new RestTemplate();
        this.restTemplate.getMessageConverters().add(0, messageConverter);
    }

    /**
     * Fetch exercises from wger API with pagination
     */
    public WgerExerciseResponse fetchExercises(Integer page, Integer pageSize, String search) {
        log.info("Fetching exercises from wger API - page: {}, pageSize: {}, search: {}",
                page, pageSize, search);

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/exercise/")
                    .queryParam("language", language)
                    .queryParam("limit", pageSize != null ? pageSize : 20);

            if (page != null && page > 1) {
                int offset = (page - 1) * (pageSize != null ? pageSize : 20);
                builder.queryParam("offset", offset);
            }

            if (search != null && !search.isEmpty()) {
                builder.queryParam("name", search);
            }

            String url = builder.toUriString();
            log.debug("wger API URL: {}", url);

            WgerExerciseResponse response = restTemplate.getForObject(url, WgerExerciseResponse.class);
            log.info("Fetched {} exercises from wger API",
                    response != null ? response.getResults().size() : 0);

            return response;

        } catch (Exception e) {
            log.error("Error fetching exercises from wger API", e);
            throw new RuntimeException("Failed to fetch exercises from external API", e);
        }
    }

    /**
     * Fetch single exercise by ID
     */
    public WgerExercise fetchExerciseById(Integer exerciseId) {
        log.info("Fetching exercise by ID: {}", exerciseId);

        try {
            String url = baseUrl + "/exercise/" + exerciseId + "/";
            WgerExercise exercise = restTemplate.getForObject(url, WgerExercise.class);
            log.info("Fetched exercise: {}", exercise != null ? exercise.getName() : "null");
            return exercise;

        } catch (Exception e) {
            log.error("Error fetching exercise by ID: {}", exerciseId, e);
            throw new RuntimeException("Failed to fetch exercise details", e);
        }
    }

    /**
     * Fetch and cache exercise categories
     */
    public Map<Integer, String> fetchCategories() {
        if (!categoryCache.isEmpty()) {
            return categoryCache;
        }

        log.info("Fetching exercise categories from wger API");

        try {
            String url = baseUrl + "/exercisecategory/";
            WgerCategoryResponse response = restTemplate.getForObject(url, WgerCategoryResponse.class);

            if (response != null && response.getResults() != null) {
                response.getResults().forEach(cat ->
                        categoryCache.put(cat.getId(), cat.getName())
                );
                log.info("Cached {} categories", categoryCache.size());
            }

            return categoryCache;

        } catch (Exception e) {
            log.error("Error fetching categories", e);
            return categoryCache;
        }
    }

    /**
     * Fetch and cache muscles
     */
    public Map<Integer, String> fetchMuscles() {
        if (!muscleCache.isEmpty()) {
            return muscleCache;
        }

        log.info("Fetching muscles from wger API");

        try {
            String url = baseUrl + "/muscle/";
            WgerMuscleResponse response = restTemplate.getForObject(url, WgerMuscleResponse.class);

            if (response != null && response.getResults() != null) {
                response.getResults().forEach(muscle ->
                        muscleCache.put(muscle.getId(), muscle.getName())
                );
                log.info("Cached {} muscles", muscleCache.size());
            }

            return muscleCache;

        } catch (Exception e) {
            log.error("Error fetching muscles", e);
            return muscleCache;
        }
    }

    /**
     * Fetch and cache equipment
     */
    public Map<Integer, String> fetchEquipment() {
        if (!equipmentCache.isEmpty()) {
            return equipmentCache;
        }

        log.info("Fetching equipment from wger API");

        try {
            String url = baseUrl + "/equipment/";
            WgerEquipmentResponse response = restTemplate.getForObject(url, WgerEquipmentResponse.class);

            if (response != null && response.getResults() != null) {
                response.getResults().forEach(eq ->
                        equipmentCache.put(eq.getId(), eq.getName())
                );
                log.info("Cached {} equipment types", equipmentCache.size());
            }

            return equipmentCache;

        } catch (Exception e) {
            log.error("Error fetching equipment", e);
            return equipmentCache;
        }
    }

    /**
     * Get category name by ID
     */
    public String getCategoryName(Integer categoryId) {
        if (categoryCache.isEmpty()) {
            fetchCategories();
        }
        return categoryCache.getOrDefault(categoryId, "Unknown");
    }

    /**
     * Get muscle name by ID
     */
    public String getMuscleName(Integer muscleId) {
        if (muscleCache.isEmpty()) {
            fetchMuscles();
        }
        return muscleCache.getOrDefault(muscleId, "Unknown");
    }

    /**
     * Get equipment name by ID
     */
    public String getEquipmentName(Integer equipmentId) {
        if (equipmentCache.isEmpty()) {
            fetchEquipment();
        }
        return equipmentCache.getOrDefault(equipmentId, "Unknown");
    }
}