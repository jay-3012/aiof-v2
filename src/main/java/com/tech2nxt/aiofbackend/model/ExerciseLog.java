package com.tech2nxt.aiofbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "exercise_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private WorkoutSession session;

    @Column(nullable = false, length = 255)
    private String exerciseId; // From wger API

    @Column(nullable = false, length = 255)
    private String exerciseName;

    @Column(length = 100)
    private String muscleGroup;

    @Column(length = 100)
    private String equipment;

    private Integer sets;

    private Integer reps;

    @Column(precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(name = "duration_seconds")
    private Integer durationSeconds; // Time spent on this exercise

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private Integer orderIndex; // Order in the workout

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExerciseLog)) return false;
        return id != null && id.equals(((ExerciseLog) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}