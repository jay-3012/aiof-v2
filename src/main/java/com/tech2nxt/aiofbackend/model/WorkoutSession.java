package com.tech2nxt.aiofbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_sessions")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(name = "workout_id")
    private Long workoutId; // Can be null for custom sessions

    @Column(name = "workout_name", length = 255)
    private String workoutName; // Store name in case workout is deleted

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(name = "total_duration_seconds")
    private Integer totalDurationSeconds; // Calculated from start/end

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkoutStatus status;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<ExerciseLog> exerciseLogs = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum WorkoutStatus {
        IN_PROGRESS,  // Session started, not completed
        COMPLETED,    // Session completed (with journal)
        ABANDONED     // Session started but not completed
    }

    // Helper methods
    public void addExerciseLog(ExerciseLog log) {
        exerciseLogs.add(log);
        log.setSession(this);
    }

    public void removeExerciseLog(ExerciseLog log) {
        exerciseLogs.remove(log);
        log.setSession(null);
    }

    /**
     * Calculate total duration when session is completed
     */
    public void calculateDuration() {
        if (startTime != null && endTime != null) {
            this.totalDurationSeconds = (int) java.time.Duration.between(startTime, endTime).getSeconds();
        }
    }
}