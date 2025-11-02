package com.tech2nxt.aiofbackend.service;

import com.tech2nxt.aiofbackend.dto.request.LogWeightRequest;
import com.tech2nxt.aiofbackend.dto.response.WeightHistoryResponse;
import com.tech2nxt.aiofbackend.dto.response.WeightLogResponse;
import com.tech2nxt.aiofbackend.exception.ConflictException;
import com.tech2nxt.aiofbackend.exception.ResourceNotFoundException;
import com.tech2nxt.aiofbackend.model.WeightLog;
import com.tech2nxt.aiofbackend.repository.WeightLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeightLogService {

    private final WeightLogRepository weightLogRepository;

    /**
     * Log body weight
     */
    @Transactional
    public WeightLogResponse logWeight(Long userId, LogWeightRequest request) {
        LocalDate logDate = request.getLogDate() != null ? request.getLogDate() : LocalDate.now();

        log.info("Logging weight for user ID: {}, date: {}", userId, logDate);

        // Check if weight already logged for this date
        Optional<WeightLog> existingLog = weightLogRepository.findByUserIdAndLogDate(userId, logDate);
        if (existingLog.isPresent()) {
            throw new ConflictException("Weight already logged for " + logDate + ". Use update endpoint to modify.");
        }

        // Create weight log
        WeightLog weightLog = WeightLog.builder()
                .userId(userId)
                .weight(request.getWeight())
                .logDate(logDate)
                .notes(request.getNotes())
                .build();

        WeightLog savedLog = weightLogRepository.save(weightLog);
        log.info("Weight logged successfully: {} kg on {}", savedLog.getWeight(), logDate);

        // Get previous weight for comparison
        List<WeightLog> previousLogs = weightLogRepository
                .findByUserIdAndDateRange(userId, logDate.minusDays(365), logDate.minusDays(1));

        BigDecimal previousWeight = previousLogs.isEmpty() ? null : previousLogs.get(0).getWeight();

        return WeightLogResponse.fromEntity(savedLog, previousWeight);
    }

    /**
     * Get weight log by ID
     */
    @Transactional(readOnly = true)
    public WeightLogResponse getWeightLogById(Long logId, Long userId) {
        log.info("Fetching weight log ID: {} for user ID: {}", logId, userId);

        WeightLog weightLog = weightLogRepository.findByIdAndUserId(logId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("WeightLog", "id", logId));

        return WeightLogResponse.fromEntity(weightLog);
    }

    /**
     * Get weight history with statistics
     */
    @Transactional(readOnly = true)
    public WeightHistoryResponse getWeightHistory(Long userId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching weight history for user ID: {} from {} to {}", userId, startDate, endDate);

        List<WeightLog> logs;
        if (startDate != null && endDate != null) {
            logs = weightLogRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        } else {
            logs = weightLogRepository.findByUserIdOrderByLogDateDesc(userId);
        }

        if (logs.isEmpty()) {
            return WeightHistoryResponse.builder()
                    .logs(new ArrayList<>())
                    .build();
        }

        // Calculate weight changes
        List<WeightLogResponse> logResponses = new ArrayList<>();
        for (int i = 0; i < logs.size(); i++) {
            WeightLog currentLog = logs.get(i);
            BigDecimal previousWeight = (i < logs.size() - 1) ? logs.get(i + 1).getWeight() : null;
            logResponses.add(WeightLogResponse.fromEntity(currentLog, previousWeight));
        }

        // Calculate statistics
        BigDecimal currentWeight = logs.get(0).getWeight();
        BigDecimal startingWeight = logs.get(logs.size() - 1).getWeight();
        BigDecimal totalChange = currentWeight.subtract(startingWeight);

        BigDecimal averageWeight = logs.stream()
                .map(WeightLog::getWeight)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(logs.size()), 2, RoundingMode.HALF_UP);

        BigDecimal lowestWeight = logs.stream()
                .map(WeightLog::getWeight)
                .min(BigDecimal::compareTo)
                .orElse(null);

        BigDecimal highestWeight = logs.stream()
                .map(WeightLog::getWeight)
                .max(BigDecimal::compareTo)
                .orElse(null);

        return WeightHistoryResponse.builder()
                .logs(logResponses)
                .currentWeight(currentWeight)
                .startingWeight(startingWeight)
                .totalChange(totalChange)
                .averageWeight(averageWeight)
                .lowestWeight(lowestWeight)
                .highestWeight(highestWeight)
                .build();
    }

    /**
     * Update weight log
     */
    @Transactional
    public WeightLogResponse updateWeightLog(Long logId, Long userId, LogWeightRequest request) {
        log.info("Updating weight log ID: {} for user ID: {}", logId, userId);

        WeightLog weightLog = weightLogRepository.findByIdAndUserId(logId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("WeightLog", "id", logId));

        // Update fields
        if (request.getWeight() != null) {
            weightLog.setWeight(request.getWeight());
        }
        if (request.getNotes() != null) {
            weightLog.setNotes(request.getNotes());
        }

        WeightLog updatedLog = weightLogRepository.save(weightLog);
        log.info("Weight log updated successfully: {}", logId);

        return WeightLogResponse.fromEntity(updatedLog);
    }

    /**
     * Delete weight log
     */
    @Transactional
    public void deleteWeightLog(Long logId, Long userId) {
        log.info("Deleting weight log ID: {} for user ID: {}", logId, userId);

        WeightLog weightLog = weightLogRepository.findByIdAndUserId(logId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("WeightLog", "id", logId));

        weightLogRepository.delete(weightLog);
        log.info("Weight log deleted successfully: {}", logId);
    }
}