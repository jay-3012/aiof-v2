package com.tech2nxt.aiofbackend.service;

import com.tech2nxt.aiofbackend.dto.request.CompleteSessionRequest;
import com.tech2nxt.aiofbackend.dto.request.LogExerciseRequest;
import com.tech2nxt.aiofbackend.dto.request.StartSessionRequest;
import com.tech2nxt.aiofbackend.dto.response.SessionHistoryResponse;
import com.tech2nxt.aiofbackend.dto.response.WorkoutSessionResponse;
import com.tech2nxt.aiofbackend.exception.BadRequestException;
import com.tech2nxt.aiofbackend.exception.ResourceNotFoundException;
import com.tech2nxt.aiofbackend.model.ExerciseLog;
import com.tech2nxt.aiofbackend.model.Workout;
import com.tech2nxt.aiofbackend.model.WorkoutSession;
import com.tech2nxt.aiofbackend.repository.JournalEntryRepository;
import com.tech2nxt.aiofbackend.repository.WorkoutRepository;
import com.tech2nxt.aiofbackend.repository.WorkoutSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkoutSessionService {

    private final WorkoutSessionRepository sessionRepository;
    private final WorkoutRepository workoutRepository;
    private final JournalEntryRepository journalRepository;

    /**
     * Start a new workout session
     */
    @Transactional
    public WorkoutSessionResponse startSession(Long userId, StartSessionRequest request) {
        log.info("Starting workout session for user ID: {}", userId);

        // Check if user already has an active session
        Optional<WorkoutSession> activeSession = sessionRepository
                .findByUserIdAndStatus(userId, WorkoutSession.WorkoutStatus.IN_PROGRESS);

        if (activeSession.isPresent()) {
            throw new BadRequestException("You already have an active workout session. " +
                    "Please complete or abandon it before starting a new one.");
        }

        // Get workout name
        String workoutName;
        if (request.getWorkoutId() != null) {
            Workout workout = workoutRepository.findByIdAndUserId(request.getWorkoutId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Workout", "id", request.getWorkoutId()));
            workoutName = workout.getName();
        } else if (request.getWorkoutName() != null && !request.getWorkoutName().isEmpty()) {
            workoutName = request.getWorkoutName();
        } else {
            throw new BadRequestException("Either workoutId or workoutName must be provided");
        }

        // Create session
        WorkoutSession session = WorkoutSession.builder()
                .userId(userId)
                .workoutId(request.getWorkoutId())
                .workoutName(workoutName)
                .startTime(LocalDateTime.now())
                .status(WorkoutSession.WorkoutStatus.IN_PROGRESS)
                .exerciseLogs(new ArrayList<>())
                .build();

        WorkoutSession savedSession = sessionRepository.save(session);
        log.info("Workout session started with ID: {}", savedSession.getId());

        return WorkoutSessionResponse.fromEntity(savedSession);
    }

    /**
     * Log an exercise during active session
     */
    @Transactional
    public WorkoutSessionResponse logExercise(Long sessionId, Long userId, LogExerciseRequest request) {
        log.info("Logging exercise for session ID: {}", sessionId);

        WorkoutSession session = sessionRepository.findByIdAndUserIdWithLogs(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutSession", "id", sessionId));

        // Verify session is active
        if (session.getStatus() != WorkoutSession.WorkoutStatus.IN_PROGRESS) {
            throw new BadRequestException("Cannot log exercises for a completed or abandoned session");
        }

        // Create exercise log
        ExerciseLog exerciseLog = ExerciseLog.builder()
                .exerciseId(request.getExerciseId())
                .exerciseName(request.getExerciseName())
                .muscleGroup(request.getMuscleGroup())
                .equipment(request.getEquipment())
                .sets(request.getSets())
                .reps(request.getReps())
                .weight(request.getWeight())
                .durationSeconds(request.getDurationSeconds())
                .notes(request.getNotes())
                .orderIndex(request.getOrderIndex())
                .build();

        session.addExerciseLog(exerciseLog);
        WorkoutSession updatedSession = sessionRepository.save(session);

        log.info("Exercise logged successfully: {}", request.getExerciseName());
        return WorkoutSessionResponse.fromEntity(updatedSession);
    }

    /**
     * Complete workout session (mark as ready for journal)
     */
    @Transactional
    public WorkoutSessionResponse completeSession(Long sessionId, Long userId, CompleteSessionRequest request) {
        log.info("Completing workout session ID: {}", sessionId);

        WorkoutSession session = sessionRepository.findByIdAndUserIdWithLogs(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutSession", "id", sessionId));

        // Verify session is active
        if (session.getStatus() != WorkoutSession.WorkoutStatus.IN_PROGRESS) {
            throw new BadRequestException("Session is already completed or abandoned");
        }

        // Optionally add/replace exercises from request
        if (request.getExercises() != null && !request.getExercises().isEmpty()) {
            session.getExerciseLogs().clear();
            for (LogExerciseRequest exerciseReq : request.getExercises()) {
                ExerciseLog log = ExerciseLog.builder()
                        .exerciseId(exerciseReq.getExerciseId())
                        .exerciseName(exerciseReq.getExerciseName())
                        .muscleGroup(exerciseReq.getMuscleGroup())
                        .equipment(exerciseReq.getEquipment())
                        .sets(exerciseReq.getSets())
                        .reps(exerciseReq.getReps())
                        .weight(exerciseReq.getWeight())
                        .durationSeconds(exerciseReq.getDurationSeconds())
                        .notes(exerciseReq.getNotes())
                        .orderIndex(exerciseReq.getOrderIndex())
                        .build();
                session.addExerciseLog(log);
            }
        }

        // Mark as completed
        session.setEndTime(LocalDateTime.now());
        session.calculateDuration();
        session.setStatus(WorkoutSession.WorkoutStatus.COMPLETED);

        WorkoutSession completedSession = sessionRepository.save(session);
        log.info("Workout session completed: {}", sessionId);

        return WorkoutSessionResponse.fromEntity(completedSession);
    }

    /**
     * Get active session for user
     */
    @Transactional(readOnly = true)
    public WorkoutSessionResponse getActiveSession(Long userId) {
        log.info("Fetching active session for user ID: {}", userId);

        WorkoutSession session = sessionRepository
                .findByUserIdAndStatus(userId, WorkoutSession.WorkoutStatus.IN_PROGRESS)
                .orElseThrow(() -> new ResourceNotFoundException("No active workout session found"));

        // Fetch exercise logs
        session.getExerciseLogs().size();

        return WorkoutSessionResponse.fromEntity(session);
    }

    /**
     * Get session by ID
     */
    @Transactional(readOnly = true)
    public WorkoutSessionResponse getSessionById(Long sessionId, Long userId) {
        log.info("Fetching session ID: {} for user ID: {}", sessionId, userId);

        WorkoutSession session = sessionRepository.findByIdAndUserIdWithLogs(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutSession", "id", sessionId));

        return WorkoutSessionResponse.fromEntity(session);
    }

    /**
     * Get workout history (paginated)
     */
    @Transactional(readOnly = true)
    public SessionHistoryResponse getSessionHistory(Long userId, Integer page, Integer pageSize) {
        log.info("Fetching session history for user ID: {}, page: {}", userId, page);

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<WorkoutSession> sessionPage = sessionRepository
                .findByUserIdOrderByStartTimeDesc(userId, pageable);

        return SessionHistoryResponse.builder()
                .sessions(sessionPage.getContent().stream()
                        .map(WorkoutSessionResponse::fromEntityMinimal)
                        .toList())
                .currentPage(sessionPage.getNumber())
                .pageSize(sessionPage.getSize())
                .totalPages(sessionPage.getTotalPages())
                .totalSessions(sessionPage.getTotalElements())
                .hasNext(sessionPage.hasNext())
                .hasPrevious(sessionPage.hasPrevious())
                .build();
    }

    /**
     * Abandon active session
     */
    @Transactional
    public void abandonSession(Long sessionId, Long userId) {
        log.info("Abandoning session ID: {} for user ID: {}", sessionId, userId);

        WorkoutSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutSession", "id", sessionId));

        if (session.getStatus() != WorkoutSession.WorkoutStatus.IN_PROGRESS) {
            throw new BadRequestException("Can only abandon IN_PROGRESS sessions");
        }

        session.setStatus(WorkoutSession.WorkoutStatus.ABANDONED);
        session.setEndTime(LocalDateTime.now());
        session.calculateDuration();

        sessionRepository.save(session);
        log.info("Session abandoned: {}", sessionId);
    }
}