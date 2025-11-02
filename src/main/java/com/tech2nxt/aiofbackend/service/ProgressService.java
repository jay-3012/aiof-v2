package com.tech2nxt.aiofbackend.service;

import com.tech2nxt.aiofbackend.dto.response.ProgressStatsResponse;
import com.tech2nxt.aiofbackend.dto.response.StreakResponse;
import com.tech2nxt.aiofbackend.model.WeightLog;
import com.tech2nxt.aiofbackend.model.WorkoutSchedule;
import com.tech2nxt.aiofbackend.model.WorkoutSession;
import com.tech2nxt.aiofbackend.repository.JournalEntryRepository;
import com.tech2nxt.aiofbackend.repository.WeightLogRepository;
import com.tech2nxt.aiofbackend.repository.WorkoutScheduleRepository;
import com.tech2nxt.aiofbackend.repository.WorkoutSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgressService {

    private final WorkoutSessionRepository sessionRepository;
    private final JournalEntryRepository journalRepository;
    private final WeightLogRepository weightLogRepository;
    private final WorkoutScheduleRepository scheduleRepository;

    /**
     * Get overall progress statistics
     */
    @Transactional(readOnly = true)
    public ProgressStatsResponse getProgressStats(Long userId) {
        log.info("Calculating progress stats for user ID: {}", userId);

        // Get all completed sessions
        List<WorkoutSession> completedSessions = sessionRepository
                .findCompletedSessionsSince(userId, LocalDateTime.now().minusDays(365));

        // Total workouts
        long totalWorkouts = completedSessions.size();

        // Calculate streaks
        StreakResponse streaks = calculateStreak(userId);

        // Total workout time
        long totalTimeSeconds = completedSessions.stream()
                .filter(s -> s.getTotalDurationSeconds() != null)
                .mapToLong(WorkoutSession::getTotalDurationSeconds)
                .sum();

        // Average workout duration
        int avgDuration = totalWorkouts > 0
                ? (int) (totalTimeSeconds / totalWorkouts)
                : 0;

        // Workouts this week
        LocalDateTime weekStart = LocalDateTime.now().with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
        int workoutsThisWeek = (int) completedSessions.stream()
                .filter(s -> s.getStartTime().isAfter(weekStart))
                .count();

        // Workouts this month
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
        int workoutsThisMonth = (int) completedSessions.stream()
                .filter(s -> s.getStartTime().isAfter(monthStart))
                .count();

        // Total journals
        long totalJournals = journalRepository.countByUserId(userId);

        // Weight stats
        Optional<WeightLog> currentWeightLog = weightLogRepository.findFirstByUserIdOrderByLogDateDesc(userId);
        Optional<WeightLog> startingWeightLog = weightLogRepository.findFirstByUserIdOrderByLogDateAsc(userId);

        BigDecimal currentWeight = currentWeightLog.map(WeightLog::getWeight).orElse(null);
        BigDecimal startingWeight = startingWeightLog.map(WeightLog::getWeight).orElse(null);
        BigDecimal weightChange = null;
        if (currentWeight != null && startingWeight != null) {
            weightChange = currentWeight.subtract(startingWeight);
        }

        // Last workout date
        LocalDateTime lastWorkoutDate = completedSessions.stream()
                .map(WorkoutSession::getStartTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return ProgressStatsResponse.builder()
                .totalWorkouts(totalWorkouts)
                .currentStreak(streaks.getCurrentStreak())
                .longestStreak(streaks.getLongestStreak())
                .totalWorkoutTimeSeconds(totalTimeSeconds)
                .totalWorkoutTimeFormatted(formatDuration(totalTimeSeconds))
                .avgWorkoutDuration(avgDuration)
                .avgWorkoutDurationFormatted(formatDuration(avgDuration))
                .workoutsThisWeek(workoutsThisWeek)
                .workoutsThisMonth(workoutsThisMonth)
                .totalJournals(totalJournals)
                .currentWeight(currentWeight)
                .startingWeight(startingWeight)
                .weightChange(weightChange)
                .lastWorkoutDate(lastWorkoutDate)
                .build();
    }

    /**
     * Calculate workout streak
     * Streak counts consecutive days where user completed their scheduled workouts
     */
    @Transactional(readOnly = true)
    public StreakResponse calculateStreak(Long userId) {
        log.info("Calculating streak for user ID: {}", userId);

        // Get user's workout schedule
        List<WorkoutSchedule> schedules = scheduleRepository.findByUserId(userId);

        // If no schedule, use simpler streak calculation (any workout = streak day)
        if (schedules.isEmpty()) {
            return calculateSimpleStreak(userId);
        }

        // Get scheduled days
        Set<DayOfWeek> scheduledDays = schedules.stream()
                .map(s -> convertToJavaDayOfWeek(s.getDayOfWeek()))
                .collect(Collectors.toSet());

        // Get completed sessions from last 90 days
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90);
        List<WorkoutSession> completedSessions = sessionRepository
                .findCompletedSessionsSince(userId, ninetyDaysAgo);

        // Group sessions by date
        Map<LocalDate, List<WorkoutSession>> sessionsByDate = completedSessions.stream()
                .collect(Collectors.groupingBy(s -> s.getStartTime().toLocalDate()));

        // Calculate current streak
        int currentStreak = 0;
        LocalDate today = LocalDate.now();
        LocalDate checkDate = today;
        LocalDate streakStartDate = today;

        while (true) {
            DayOfWeek dayOfWeek = checkDate.getDayOfWeek();

            // If this day is scheduled
            if (scheduledDays.contains(dayOfWeek)) {
                // Check if user completed a workout on this day
                if (sessionsByDate.containsKey(checkDate)) {
                    currentStreak++;
                    streakStartDate = checkDate;
                } else {
                    // Streak broken
                    break;
                }
            }

            // Move to previous day
            checkDate = checkDate.minusDays(1);

            // Stop after 90 days
            if (ChronoUnit.DAYS.between(checkDate, today) > 90) {
                break;
            }
        }

        // Calculate longest streak (simplified - check last 90 days)
        int longestStreak = calculateLongestStreak(scheduledDays, sessionsByDate, 90);

        // Determine if active today
        boolean isActiveToday = scheduledDays.contains(today.getDayOfWeek()) &&
                sessionsByDate.containsKey(today);

        // Days until break
        int daysUntilBreak = calculateDaysUntilBreak(scheduledDays, today);

        // Generate message
        String message = generateStreakMessage(currentStreak, isActiveToday);

        return StreakResponse.builder()
                .currentStreak(currentStreak)
                .longestStreak(Math.max(currentStreak, longestStreak))
                .streakStartDate(currentStreak > 0 ? streakStartDate : null)
                .isActiveToday(isActiveToday)
                .daysUntilBreak(daysUntilBreak)
                .message(message)
                .build();
    }

    /**
     * Simple streak calculation (any workout = streak day)
     */
    private StreakResponse calculateSimpleStreak(Long userId) {
        LocalDateTime sixtyDaysAgo = LocalDateTime.now().minusDays(60);
        List<WorkoutSession> sessions = sessionRepository.findCompletedSessionsSince(userId, sixtyDaysAgo);

        Set<LocalDate> workoutDates = sessions.stream()
                .map(s -> s.getStartTime().toLocalDate())
                .collect(Collectors.toSet());

        int currentStreak = 0;
        LocalDate today = LocalDate.now();
        LocalDate checkDate = today;
        LocalDate streakStartDate = today;

        while (workoutDates.contains(checkDate)) {
            currentStreak++;
            streakStartDate = checkDate;
            checkDate = checkDate.minusDays(1);
        }

        boolean isActiveToday = workoutDates.contains(today);
        String message = generateStreakMessage(currentStreak, isActiveToday);

        return StreakResponse.builder()
                .currentStreak(currentStreak)
                .longestStreak(currentStreak)
                .streakStartDate(currentStreak > 0 ? streakStartDate : null)
                .isActiveToday(isActiveToday)
                .daysUntilBreak(isActiveToday ? 1 : 0)
                .message(message)
                .build();
    }

    /**
     * Calculate longest streak in given period
     */
    private int calculateLongestStreak(
            Set<DayOfWeek> scheduledDays,
            Map<LocalDate, List<WorkoutSession>> sessionsByDate,
            int daysToCheck) {

        int longestStreak = 0;
        int tempStreak = 0;
        LocalDate checkDate = LocalDate.now();

        for (int i = 0; i < daysToCheck; i++) {
            DayOfWeek dayOfWeek = checkDate.getDayOfWeek();

            if (scheduledDays.contains(dayOfWeek)) {
                if (sessionsByDate.containsKey(checkDate)) {
                    tempStreak++;
                    longestStreak = Math.max(longestStreak, tempStreak);
                } else {
                    tempStreak = 0;
                }
            }

            checkDate = checkDate.minusDays(1);
        }

        return longestStreak;
    }

    /**
     * Calculate days until streak will break
     */
    private int calculateDaysUntilBreak(Set<DayOfWeek> scheduledDays, LocalDate today) {
        LocalDate checkDate = today.plusDays(1);
        for (int days = 1; days <= 7; days++) {
            if (scheduledDays.contains(checkDate.getDayOfWeek())) {
                return days;
            }
            checkDate = checkDate.plusDays(1);
        }
        return 7;
    }

    /**
     * Convert model DayOfWeek to java.time.DayOfWeek
     */
    private DayOfWeek convertToJavaDayOfWeek(WorkoutSchedule.DayOfWeek day) {
        return DayOfWeek.valueOf(day.name());
    }

    /**
     * Generate motivational streak message
     */
    private String generateStreakMessage(int streak, boolean isActiveToday) {
        if (streak == 0) {
            return "Start your streak today! Complete a workout to begin.";
        } else if (streak == 1) {
            return "Great start! Keep it going tomorrow!";
        } else if (streak < 7) {
            return String.format("Awesome! %d days in a row. Keep pushing!", streak);
        } else if (streak < 30) {
            return String.format("Amazing! %d-day streak! You're on fire! 🔥", streak);
        } else {
            return String.format("Incredible! %d-day streak! You're unstoppable! 💪", streak);
        }
    }

    /**
     * Format duration in seconds to HH:mm:ss
     */
    private String formatDuration(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
}