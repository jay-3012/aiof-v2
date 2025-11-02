package com.tech2nxt.aiofbackend.repository;

import com.tech2nxt.aiofbackend.model.JournalEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    /**
     * Find journal by ID and user ID (for authorization)
     */
    Optional<JournalEntry> findByIdAndUserId(Long id, Long userId);

    /**
     * Find journal by session ID
     */
    Optional<JournalEntry> findBySessionId(Long sessionId);

    /**
     * Find journal by session ID and user ID
     */
    Optional<JournalEntry> findBySessionIdAndUserId(Long sessionId, Long userId);

    /**
     * Check if journal exists for session
     */
    boolean existsBySessionId(Long sessionId);

    /**
     * Find all journals for user (paginated)
     */
    Page<JournalEntry> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Find journals within date range
     */
    List<JournalEntry> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate);

    /**
     * Count journals for user
     */
    long countByUserId(Long userId);
}