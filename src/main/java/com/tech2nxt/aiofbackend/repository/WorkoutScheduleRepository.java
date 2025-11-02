package com.tech2nxt.aiofbackend.repository;

import com.tech2nxt.aiofbackend.model.WorkoutSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutScheduleRepository extends JpaRepository<WorkoutSchedule, Long> {

    /**
     * Find all schedules for a workout
     */
    List<WorkoutSchedule> findByWorkoutId(Long workoutId);

    /**
     * Find all schedules for a user on a specific day
     */
    @Query("SELECT s FROM WorkoutSchedule s " +
            "WHERE s.workout.userId = :userId " +
            "AND s.dayOfWeek = :dayOfWeek")
    List<WorkoutSchedule> findByUserIdAndDayOfWeek(
            @Param("userId") Long userId,
            @Param("dayOfWeek") WorkoutSchedule.DayOfWeek dayOfWeek);

    /**
     * Find all schedules for a user (entire week)
     */
    @Query("SELECT s FROM WorkoutSchedule s " +
            "WHERE s.workout.userId = :userId " +
            "ORDER BY s.dayOfWeek")
    List<WorkoutSchedule> findByUserId(@Param("userId") Long userId);

    /**
     * Delete all schedules for a workout
     */
    void deleteByWorkoutId(Long workoutId);
}