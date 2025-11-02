package com.tech2nxt.aiofbackend.repository;

import com.tech2nxt.aiofbackend.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    /**
     * Find all workouts for a specific user
     */
    List<Workout> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find workout by ID and user ID (for authorization)
     */
    Optional<Workout> findByIdAndUserId(Long id, Long userId);

    /**
     * Find workout with exercises eagerly loaded
     */
    @Query("SELECT DISTINCT w FROM Workout w " +
            "LEFT JOIN FETCH w.exercises " +
            "WHERE w.id = :workoutId AND w.userId = :userId")
    Optional<Workout> findByIdAndUserIdWithExercises(
            @Param("workoutId") Long workoutId,
            @Param("userId") Long userId);

    /**
     * Find workout with schedules eagerly loaded
     */
    @Query("SELECT DISTINCT w FROM Workout w " +
            "LEFT JOIN FETCH w.schedules " +
            "WHERE w.id = :workoutId AND w.userId = :userId")
    Optional<Workout> findByIdAndUserIdWithSchedules(
            @Param("workoutId") Long workoutId,
            @Param("userId") Long userId);

    /**
     * Find workout with all relationships eagerly loaded
     * Using Set for schedules avoids MultipleBagFetchException
     */
    @Query("SELECT DISTINCT w FROM Workout w " +
            "LEFT JOIN FETCH w.exercises " +
            "LEFT JOIN FETCH w.schedules " +
            "WHERE w.id = :workoutId AND w.userId = :userId")
    Optional<Workout> findByIdAndUserIdWithAll(
            @Param("workoutId") Long workoutId,
            @Param("userId") Long userId);

    /**
     * Check if workout exists for user
     */
    boolean existsByIdAndUserId(Long id, Long userId);

    /**
     * Count workouts for user
     */
    long countByUserId(Long userId);
}