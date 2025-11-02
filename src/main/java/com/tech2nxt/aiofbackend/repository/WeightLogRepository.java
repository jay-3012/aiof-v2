package com.tech2nxt.aiofbackend.repository;

import com.tech2nxt.aiofbackend.model.WeightLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeightLogRepository extends JpaRepository<WeightLog, Long> {

    /**
     * Find weight log by ID and user ID
     */
    Optional<WeightLog> findByIdAndUserId(Long id, Long userId);

    /**
     * Find weight log by date and user
     */
    Optional<WeightLog> findByUserIdAndLogDate(Long userId, LocalDate logDate);

    /**
     * Find all weight logs for user, ordered by date
     */
    List<WeightLog> findByUserIdOrderByLogDateDesc(Long userId);

    /**
     * Find weight logs within date range
     */
    @Query("SELECT w FROM WeightLog w " +
            "WHERE w.userId = :userId " +
            "AND w.logDate BETWEEN :startDate AND :endDate " +
            "ORDER BY w.logDate DESC")
    List<WeightLog> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Get latest weight log for user
     */
    Optional<WeightLog> findFirstByUserIdOrderByLogDateDesc(Long userId);

    /**
     * Get oldest weight log for user
     */
    Optional<WeightLog> findFirstByUserIdOrderByLogDateAsc(Long userId);

    /**
     * Count weight logs for user
     */
    long countByUserId(Long userId);
}