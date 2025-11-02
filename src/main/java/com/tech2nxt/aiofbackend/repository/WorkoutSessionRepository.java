package com.tech2nxt.aiofbackend.repository;

import com.tech2nxt.aiofbackend.model.WorkoutSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {

    /**
     * Find session by ID and user ID (for authorization)
     */
    Optional<WorkoutSession> findByIdAndUserId(Long id, Long userId);

    /**
     * Find session with exercise logs eagerly loaded
     */
    @Query("SELECT DISTINCT s FROM WorkoutSession s " +
            "LEFT JOIN FETCH s.exerciseLogs " +
            "WHERE s.id = :sessionId AND s.userId = :userId")
    Optional<WorkoutSession> findByIdAndUserIdWithLogs(
            @Param("sessionId") Long sessionId,
            @Param("userId") Long userId);

    /**
     * Find all sessions for user (paginated)
     */
    Page<WorkoutSession> findByUserIdOrderByStartTimeDesc(Long userId, Pageable pageable);

    /**
     * Find completed sessions for user (paginated)
     */
    Page<WorkoutSession> findByUserIdAndStatusOrderByStartTimeDesc(
            Long userId,
            WorkoutSession.WorkoutStatus status,
            Pageable pageable);

    /**
     * Find active session for user (IN_PROGRESS)
     */
    Optional<WorkoutSession> findByUserIdAndStatus(
            Long userId,
            WorkoutSession.WorkoutStatus status);

    /**
     * Count completed sessions for user
     */
    long countByUserIdAndStatus(Long userId, WorkoutSession.WorkoutStatus status);

    /**
     * Find sessions within date range
     */
    @Query("SELECT s FROM WorkoutSession s " +
            "WHERE s.userId = :userId " +
            "AND s.startTime BETWEEN :startDate AND :endDate " +
            "ORDER BY s.startTime DESC")
    List<WorkoutSession> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find completed sessions with journals (for streak calculation)
     * Note: Journal check will be added in Phase 5
     */
    @Query("SELECT s FROM WorkoutSession s " +
            "WHERE s.userId = :userId " +
            "AND s.status = 'COMPLETED' " +
            "AND s.startTime >= :fromDate " +
            "ORDER BY s.startTime DESC")
    List<WorkoutSession> findCompletedSessionsSince(
            @Param("userId") Long userId,
            @Param("fromDate") LocalDateTime fromDate);
}