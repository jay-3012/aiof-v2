package com.tech2nxt.aiofbackend.service;

import com.tech2nxt.aiofbackend.dto.request.CreateWorkoutRequest;
import com.tech2nxt.aiofbackend.dto.request.UpdateWorkoutRequest;
import com.tech2nxt.aiofbackend.dto.request.WorkoutExerciseRequest;
import com.tech2nxt.aiofbackend.dto.response.WeeklyScheduleResponse;
import com.tech2nxt.aiofbackend.dto.response.WorkoutResponse;
import com.tech2nxt.aiofbackend.exception.BadRequestException;
import com.tech2nxt.aiofbackend.exception.ForbiddenException;
import com.tech2nxt.aiofbackend.exception.ResourceNotFoundException;
import com.tech2nxt.aiofbackend.model.Workout;
import com.tech2nxt.aiofbackend.model.WorkoutExercise;
import com.tech2nxt.aiofbackend.model.WorkoutSchedule;
import com.tech2nxt.aiofbackend.repository.WorkoutRepository;
import com.tech2nxt.aiofbackend.repository.WorkoutScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final WorkoutScheduleRepository workoutScheduleRepository;

    /**
     * Create new workout
     */
    @Transactional
    public WorkoutResponse createWorkout(Long userId, CreateWorkoutRequest request) {
        log.info("Creating workout for user ID: {}, name: {}", userId, request.getName());

        // Create workout entity
        Workout workout = Workout.builder()
                .userId(userId)
                .name(request.getName())
                .description(request.getDescription())
                .exercises(new ArrayList<>())
                .schedules(new HashSet<>())
                .build();

        // Add exercises
        for (WorkoutExerciseRequest exerciseReq : request.getExercises()) {
            WorkoutExercise exercise = WorkoutExercise.builder()
                    .exerciseId(exerciseReq.getExerciseId())
                    .exerciseName(exerciseReq.getExerciseName())
                    .muscleGroup(exerciseReq.getMuscleGroup())
                    .equipment(exerciseReq.getEquipment())
                    .sets(exerciseReq.getSets())
                    .reps(exerciseReq.getReps())
                    .weight(exerciseReq.getWeight())
                    .notes(exerciseReq.getNotes())
                    .orderIndex(exerciseReq.getOrderIndex())
                    .build();
            workout.addExercise(exercise);
        }

        // Add schedules
        if (request.getScheduledDays() != null && !request.getScheduledDays().isEmpty()) {
            for (String day : request.getScheduledDays()) {
                try {
                    WorkoutSchedule schedule = WorkoutSchedule.builder()
                            .dayOfWeek(WorkoutSchedule.DayOfWeek.valueOf(day.toUpperCase()))
                            .build();
                    workout.addSchedule(schedule);
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException("Invalid day of week: " + day);
                }
            }
        }

        Workout savedWorkout = workoutRepository.save(workout);
        log.info("Workout created successfully with ID: {}", savedWorkout.getId());

        return WorkoutResponse.fromEntity(savedWorkout);
    }

    /**
     * Get workout by ID
     */
    @Transactional(readOnly = true)
    public WorkoutResponse getWorkoutById(Long workoutId, Long userId) {
        log.info("Fetching workout ID: {} for user ID: {}", workoutId, userId);

        // Fetch workout with exercises (can't fetch both collections in one query)
        Workout workout = workoutRepository.findByIdAndUserIdWithAll(workoutId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout", "id", workoutId));

        // Schedules are lazy-loaded automatically via Hibernate
        // Access them within transaction to trigger fetch
        workout.getSchedules().size();

        return WorkoutResponse.fromEntity(workout);
    }

    /**
     * Get all workouts for user
     */
    @Transactional(readOnly = true)
    public List<WorkoutResponse> getAllWorkouts(Long userId) {
        log.info("Fetching all workouts for user ID: {}", userId);

        List<Workout> workouts = workoutRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return workouts.stream()
                .map(WorkoutResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Update workout
     */
    @Transactional
    public WorkoutResponse updateWorkout(Long workoutId, Long userId, UpdateWorkoutRequest request) {
        log.info("Updating workout ID: {} for user ID: {}", workoutId, userId);

        Workout workout = workoutRepository.findByIdAndUserIdWithAll(workoutId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout", "id", workoutId));

        // Update basic fields
        if (request.getName() != null) {
            workout.setName(request.getName());
        }
        if (request.getDescription() != null) {
            workout.setDescription(request.getDescription());
        }

        // Update exercises (replace all)
        if (request.getExercises() != null) {
            workout.getExercises().clear();
            for (WorkoutExerciseRequest exerciseReq : request.getExercises()) {
                WorkoutExercise exercise = WorkoutExercise.builder()
                        .exerciseId(exerciseReq.getExerciseId())
                        .exerciseName(exerciseReq.getExerciseName())
                        .muscleGroup(exerciseReq.getMuscleGroup())
                        .equipment(exerciseReq.getEquipment())
                        .sets(exerciseReq.getSets())
                        .reps(exerciseReq.getReps())
                        .weight(exerciseReq.getWeight())
                        .notes(exerciseReq.getNotes())
                        .orderIndex(exerciseReq.getOrderIndex())
                        .build();
                workout.addExercise(exercise);
            }
        }

        // Update schedules (replace all)
        if (request.getScheduledDays() != null) {
            workout.getSchedules().clear();
            for (String day : request.getScheduledDays()) {
                try {
                    WorkoutSchedule schedule = WorkoutSchedule.builder()
                            .dayOfWeek(WorkoutSchedule.DayOfWeek.valueOf(day.toUpperCase()))
                            .build();
                    workout.addSchedule(schedule);
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException("Invalid day of week: " + day);
                }
            }
        }

        Workout updatedWorkout = workoutRepository.save(workout);
        log.info("Workout updated successfully: {}", workoutId);

        return WorkoutResponse.fromEntity(updatedWorkout);
    }

    /**
     * Delete workout
     */
    @Transactional
    public void deleteWorkout(Long workoutId, Long userId) {
        log.info("Deleting workout ID: {} for user ID: {}", workoutId, userId);

        Workout workout = workoutRepository.findByIdAndUserId(workoutId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout", "id", workoutId));

        workoutRepository.delete(workout);
        log.info("Workout deleted successfully: {}", workoutId);
    }

    /**
     * Get weekly schedule for user
     */
    @Transactional(readOnly = true)
    public List<WeeklyScheduleResponse> getWeeklySchedule(Long userId) {
        log.info("Fetching weekly schedule for user ID: {}", userId);

        List<WorkoutSchedule> schedules = workoutScheduleRepository.findByUserId(userId);

        // Group by day of week
        Map<WorkoutSchedule.DayOfWeek, List<WorkoutSchedule>> schedulesByDay = schedules.stream()
                .collect(Collectors.groupingBy(WorkoutSchedule::getDayOfWeek));

        // Create response for each day
        List<WeeklyScheduleResponse> weeklySchedule = new ArrayList<>();
        for (WorkoutSchedule.DayOfWeek day : WorkoutSchedule.DayOfWeek.values()) {
            List<WorkoutSchedule> daySchedules = schedulesByDay.getOrDefault(day, Collections.emptyList());
            List<WorkoutResponse> workouts = daySchedules.stream()
                    .map(s -> WorkoutResponse.fromEntityMinimal(s.getWorkout()))
                    .collect(Collectors.toList());

            weeklySchedule.add(WeeklyScheduleResponse.builder()
                    .dayOfWeek(day.name())
                    .workouts(workouts)
                    .workoutCount(workouts.size())
                    .build());
        }

        return weeklySchedule;
    }

    /**
     * Get workouts scheduled for a specific day
     */
    @Transactional(readOnly = true)
    public List<WorkoutResponse> getWorkoutsByDay(Long userId, String day) {
        log.info("Fetching workouts for user ID: {} on day: {}", userId, day);

        try {
            WorkoutSchedule.DayOfWeek dayOfWeek = WorkoutSchedule.DayOfWeek.valueOf(day.toUpperCase());
            List<WorkoutSchedule> schedules = workoutScheduleRepository
                    .findByUserIdAndDayOfWeek(userId, dayOfWeek);

            return schedules.stream()
                    .map(s -> WorkoutResponse.fromEntity(s.getWorkout()))
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid day of week: " + day);
        }
    }
}