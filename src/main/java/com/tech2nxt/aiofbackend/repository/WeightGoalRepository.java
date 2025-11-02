package com.tech2nxt.aiofbackend.repository;

import com.tech2nxt.aiofbackend.model.WeightGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeightGoalRepository extends JpaRepository<WeightGoal, Long> {

    /**
     * Find goal by ID and user ID
     */
    Optional<WeightGoal> findByIdAndUserId(Long id, Long userId);

    /**
     * Find all goals for user
     */
    List<WeightGoal> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find active goal for user
     */
    Optional<WeightGoal> findByUserIdAndStatus(Long userId, WeightGoal.GoalStatus status);

    /**
     * Find active goals for user
     */
    List<WeightGoal> findByUserIdAndStatusOrderByCreatedAtDesc(
            Long userId,
            WeightGoal.GoalStatus status);

    /**
     * Count goals by status
     */
    long countByUserIdAndStatus(Long userId, WeightGoal.GoalStatus status);
}