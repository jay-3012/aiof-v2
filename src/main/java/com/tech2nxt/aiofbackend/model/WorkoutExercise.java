package com.tech2nxt.aiofbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "workout_exercises")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @Column(nullable = false, length = 255)
    private String exerciseId; // ID from wger API (e.g., "123")

    @Column(nullable = false, length = 255)
    private String exerciseName; // Exercise name for display

    @Column(length = 100)
    private String muscleGroup; // e.g., "Chest", "Back", "Legs"

    @Column(length = 100)
    private String equipment; // e.g., "Barbell", "Dumbbell", "Bodyweight"

    private Integer sets;

    private Integer reps;

    @Column(precision = 5, scale = 2)
    private BigDecimal weight; // Weight in kg or lbs

    @Column(length = 500)
    private String notes; // User notes for this exercise

    @Column(nullable = false)
    private Integer orderIndex; // Order in the workout (0, 1, 2, ...)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkoutExercise)) return false;
        return id != null && id.equals(((WorkoutExercise) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}