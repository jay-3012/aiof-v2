package com.tech2nxt.aiofbackend.repository;

import com.tech2nxt.aiofbackend.model.ExerciseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Long> {

    /**
     * Find all exercise logs for a session, ordered by orderIndex
     */
    List<ExerciseLog> findBySessionIdOrderByOrderIndexAsc(Long sessionId);

    /**
     * Delete all exercise logs for a session
     */
    void deleteBySessionId(Long sessionId);

    /**
     * Count exercises in a session
     */
    long countBySessionId(Long sessionId);
}