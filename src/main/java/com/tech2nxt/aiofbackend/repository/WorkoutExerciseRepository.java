package com.tech2nxt.aiofbackend.repository;

import com.tech2nxt.aiofbackend.model.WorkoutExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {

    /**
     * Find all exercises for a workout, ordered by orderIndex
     */
    List<WorkoutExercise> findByWorkoutIdOrderByOrderIndexAsc(Long workoutId);

    /**
     * Delete all exercises for a workout
     */
    void deleteByWorkoutId(Long workoutId);
}