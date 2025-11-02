package com.tech2nxt.aiofbackend.service;

import com.tech2nxt.aiofbackend.dto.request.CreateJournalRequest;
import com.tech2nxt.aiofbackend.dto.response.JournalEntryResponse;
import com.tech2nxt.aiofbackend.exception.BadRequestException;
import com.tech2nxt.aiofbackend.exception.ConflictException;
import com.tech2nxt.aiofbackend.exception.ResourceNotFoundException;
import com.tech2nxt.aiofbackend.model.JournalEntry;
import com.tech2nxt.aiofbackend.model.WorkoutSession;
import com.tech2nxt.aiofbackend.repository.JournalEntryRepository;
import com.tech2nxt.aiofbackend.repository.WorkoutSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JournalService {

    private final JournalEntryRepository journalRepository;
    private final WorkoutSessionRepository sessionRepository;
    private final ImageStorageService imageStorageService;

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Create journal entry with image upload
     */
    @Transactional
    public JournalEntryResponse createJournal(
            Long userId,
            CreateJournalRequest request,
            MultipartFile image) {

        log.info("Creating journal for user ID: {}, session ID: {}", userId, request.getSessionId());

        // Verify session exists and belongs to user
        WorkoutSession session = sessionRepository.findByIdAndUserId(request.getSessionId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutSession", "id", request.getSessionId()));

        // Verify session is completed
        if (session.getStatus() != WorkoutSession.WorkoutStatus.COMPLETED) {
            throw new BadRequestException("Can only create journal for completed sessions");
        }

        // Check if journal already exists for this session
        if (journalRepository.existsBySessionId(request.getSessionId())) {
            throw new ConflictException("Journal already exists for this workout session");
        }

        // Upload image
        String imageFilename = null;
        if (image != null && !image.isEmpty()) {
            imageFilename = imageStorageService.saveImage(image, userId);
        }

        // Create journal entry
        JournalEntry journal = JournalEntry.builder()
                .userId(userId)
                .sessionId(request.getSessionId())
                .imageUrl(imageFilename)
                .description(request.getDescription())
                .build();

        JournalEntry savedJournal = journalRepository.save(journal);
        log.info("Journal created successfully with ID: {}", savedJournal.getId());

        String baseUrl = "http://localhost:" + serverPort;
        return JournalEntryResponse.fromEntity(savedJournal, baseUrl);
    }

    /**
     * Get journal by ID
     */
    @Transactional(readOnly = true)
    public JournalEntryResponse getJournalById(Long journalId, Long userId) {
        log.info("Fetching journal ID: {} for user ID: {}", journalId, userId);

        JournalEntry journal = journalRepository.findByIdAndUserId(journalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("JournalEntry", "id", journalId));

        String baseUrl = "http://localhost:" + serverPort;
        return JournalEntryResponse.fromEntity(journal, baseUrl);
    }

    /**
     * Get journal by session ID
     */
    @Transactional(readOnly = true)
    public JournalEntryResponse getJournalBySession(Long sessionId, Long userId) {
        log.info("Fetching journal for session ID: {}", sessionId);

        JournalEntry journal = journalRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Journal not found for this session"));

        String baseUrl = "http://localhost:" + serverPort;
        return JournalEntryResponse.fromEntity(journal, baseUrl);
    }

    /**
     * Get all journals for user (paginated)
     */
    @Transactional(readOnly = true)
    public Page<JournalEntryResponse> getAllJournals(Long userId, Integer page, Integer pageSize) {
        log.info("Fetching journals for user ID: {}, page: {}", userId, page);

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<JournalEntry> journalPage = journalRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        String baseUrl = "http://localhost:" + serverPort;
        return journalPage.map(journal -> JournalEntryResponse.fromEntity(journal, baseUrl));
    }

    /**
     * Update journal entry
     */
    @Transactional
    public JournalEntryResponse updateJournal(
            Long journalId,
            Long userId,
            String description,
            MultipartFile newImage) {

        log.info("Updating journal ID: {} for user ID: {}", journalId, userId);

        JournalEntry journal = journalRepository.findByIdAndUserId(journalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("JournalEntry", "id", journalId));

        // Update description
        if (description != null && !description.isEmpty()) {
            journal.setDescription(description);
        }

        // Update image if provided
        if (newImage != null && !newImage.isEmpty()) {
            // Delete old image
            if (journal.getImageUrl() != null) {
                imageStorageService.deleteImage(journal.getImageUrl());
            }
            // Upload new image
            String newImageFilename = imageStorageService.saveImage(newImage, userId);
            journal.setImageUrl(newImageFilename);
        }

        JournalEntry updatedJournal = journalRepository.save(journal);
        log.info("Journal updated successfully: {}", journalId);

        String baseUrl = "http://localhost:" + serverPort;
        return JournalEntryResponse.fromEntity(updatedJournal, baseUrl);
    }

    /**
     * Delete journal entry
     */
    @Transactional
    public void deleteJournal(Long journalId, Long userId) {
        log.info("Deleting journal ID: {} for user ID: {}", journalId, userId);

        JournalEntry journal = journalRepository.findByIdAndUserId(journalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("JournalEntry", "id", journalId));

        // Delete image file
        if (journal.getImageUrl() != null) {
            imageStorageService.deleteImage(journal.getImageUrl());
        }

        journalRepository.delete(journal);
        log.info("Journal deleted successfully: {}", journalId);
    }

    /**
     * Check if session has journal
     */
    @Transactional(readOnly = true)
    public boolean hasJournal(Long sessionId) {
        return journalRepository.existsBySessionId(sessionId);
    }
}