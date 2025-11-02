//package com.tech2nxt.aiofbackend.service;
//
//import com.tech2nxt.aiofbackend.dto.request.CreateWeightGoalRequest;
//import com.tech2nxt.aiofbackend.exception.BadRequestException;
//import com.tech2nxt.aiofbackend.exception.ResourceNotFoundException;
//import com.tech2nxt.aiofbackend.model.WeightGoal;
//import com.tech2nxt.aiofbackend.model.WeightLog;
//import com.tech2nxt.aiofbackend.repository.WeightGoalRepository;
//import com.tech2nxt.aiofbackend.repository.WeightLogRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class WeightGoalService {
//
//    private final WeightGoalRepository goalRepository;
//    private final WeightLogRepository weightLogRepository;
//
//    /**
//     * Create weight goal
//     */
//    @Transactional
//    public WeightGoalResponse createWeightGoal(Long userId, CreateWeightGoalRequest request) {
//        log.info("Creating weight goal for user ID: {}", userId);
//
//        // Validate dates
//        if (request.getTargetDate().isBefore(request.getStartDate())) {
//            throw new BadRequestException("Target date must be after start date");
//        }
//
//        // Determine goal type
//        WeightGoal.GoalType goalType;
//        if (request.getTargetWeight().compareTo(request.getStartingWeight()) < 0) {
//            goalType = WeightGoal.GoalType.WEIGHT_LOSS;
//        } else if (request.getTargetWeight().compareTo(request.getStartingWeight()) > 0) {
//            goalType = WeightGoal.GoalType.WEIGHT_GAIN;
//        } else {
//            goalType = WeightGoal.GoalType.MAINTENANCE;
//        }
//
//        // Create goal
//        WeightGoal goal = WeightGoal.builder()
//                .userId(userId)
//                .startingWeight(request.getStartingWeight())
//                .targetWeight(request.getTargetWeight())
//                .startDate(request.getStartDate())
//                .targetDate(request.getTargetDate())
//                .goalType(goalType)
//                .status(WeightGoal.GoalStatus.ACTIVE)
//                .notes(request.getNotes())
//                .build();
//
//        WeightGoal savedGoal = goalRepository.save(goal);
//        log.info("Weight goal created successfully with ID: {}", savedGoal.getId());
//
//        // Get current weight
//        BigDecimal currentWeight = getCurrentWeight(userId);
//
//        return WeightGoalResponse.fromEntity(savedGoal, currentWeight);
//    }
//
//    /**
//     * Get weight goal by ID
//     */
//    @Transactional(readOnly = true)
//    public WeightGoalResponse getWeightGoalById(Long goalId, Long userId) {
//        log.info("Fetching weight goal ID: {} for user ID: {}", goalId, userId);
//
//        WeightGoal goal = goalRepository.findByIdAndUserId(goalId, userId)
//                .orElseThrow(() -> new ResourceNotFoundException("WeightGoal", "id", goalId));
//
//        BigDecimal currentWeight = getCurrentWeight(userId);
//
//        return WeightGoalResponse.fromEntity(goal, currentWeight);
//    }
//
//    /**
//     * Get active weight goal
//     */
//    @Transactional(readOnly = true)
//    public WeightGoalResponse getActiveGoal(Long userId) {
//        log.info("Fetching active weight goal for user ID: {}", userId);
//
//        WeightGoal goal = goalRepository.findByUserIdAndStatus(userId, WeightGoal.GoalStatus.ACTIVE)
//                .orElseThrow(() -> new ResourceNotFoundException("No active weight goal found"));
//
//        BigDecimal currentWeight = getCurrentWeight(userId);
//
//        return WeightGoalResponse.fromEntity(goal, currentWeight);
//    }
//
//    /**
//     * Get all weight goals
//     */
//    @Transactional(readOnly = true)
//    public List<WeightGoalResponse> getAllGoals(Long userId) {
//        log.info("Fetching all weight goals for user ID: {}", userId);
//
//        List<WeightGoal> goals = goalRepository.findByUserIdOrderByCreatedAtDesc(userId);
//        BigDecimal currentWeight = getCurrentWeight(userId);
//
//        return goals.stream()
//                .map(goal -> WeightGoalResponse.fromEntity(goal, currentWeight))
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Update weight goal
//     */
//    @Transactional
//    public WeightGoalResponse updateWeightGoal(
//            Long goalId,
//            Long userId,
//            CreateWeightGoalRequest request) {
//
//        log.info("Updating weight goal ID: {} for user ID: {}", goalId, userId);
//
//        WeightGoal goal = goalRepository.findByIdAndUserId(goalId, userId)
//                .orElseThrow(() -> new ResourceNotFoundException("WeightGoal", "id", goalId));
//
//        // Update fields
//        if (request.getTargetWeight() != null) {
//            goal.setTargetWeight(request.getTargetWeight());
//
//            // Recalculate goal type
//            if (request.getTargetWeight().compareTo(goal.getStartingWeight()) < 0) {
//                goal.setGoalType(WeightGoal.GoalType.WEIGHT_LOSS);
//            } else if (request.getTargetWeight().compareTo(goal.getStartingWeight()) > 0) {
//                goal.setGoalType(WeightGoal.GoalType.WEIGHT_GAIN);
//            } else {
//                goal.setGoalType(WeightGoal.GoalType.MAINTENANCE);
//            }
//        }
//
//        if (request.getTargetDate() != null) {
//            goal.setTargetDate(request.getTargetDate());
//        }
//
//        if (request.getNotes() != null) {
//            goal.setNotes(request.getNotes());
//        }
//
//        WeightGoal updatedGoal = goalRepository.save(goal);
//        log.info("Weight goal updated successfully: {}", goalId);
//
//        BigDecimal currentWeight = getCurrentWeight(userId);
//
//        return WeightGoalResponse.fromEntity(updatedGoal, currentWeight);
//    }
//
//    /**
//     * Complete weight goal
//     */
//    @Transactional
//    public WeightGoalResponse completeGoal(Long goalId, Long userId) {
//        log.info("Completing weight goal ID: {} for user ID: {}", goalId, userId);
//
//        WeightGoal goal = goalRepository.findByIdAndUserId(goalId, userId)
//                .orElseThrow(() -> new ResourceNotFoundException("WeightGoal", "id", goalId));
//
//        goal.setStatus(WeightGoal.GoalStatus.COMPLETED);
//        WeightGoal updatedGoal = goalRepository.save(goal);
//
//        log.info("Weight goal completed: {}", goalId);
//
//        BigDecimal currentWeight = getCurrentWeight(userId);
//
//        return WeightGoalResponse.fromEntity(updatedGoal, currentWeight);
//    }
//
//    /**
//     * Abandon weight goal
//     */
//    @Transactional
//    public void abandonGoal(Long goalId, Long userId) {
//        log.info("Abandoning weight goal ID: {} for user ID: {}", goalId, userId);
//
//        WeightGoal goal = goalRepository.findByIdAndUserId(goalId, userId)
//                .orElseThrow(() -> new ResourceNotFoundException("WeightGoal", "id", goalId));
//
//        goal.setStatus(WeightGoal.GoalStatus.ABANDONED);
//        goalRepository.save(goal);
//
//        log.info("Weight goal abandoned: {}", goalId);
//    }
//
//    /**
//     * Delete weight goal
//     */
//    @Transactional
//    public void deleteGoal(Long goalId, Long userId) {
//        log.info("Deleting weight goal ID: {} for user ID: {}", goalId, userId);
//
//        WeightGoal goal = goalRepository.findByIdAndUserId(goalId, userId)
//                .orElseThrow(() -> new ResourceNotFoundException("WeightGoal", "id", goalId));
//
//        goalRepository.delete(goal);
//        log.info("Weight goal deleted: {}", goalId);
//    }
//
//    /**
//     * Get current weight for user
//     */
//    private BigDecimal getCurrentWeight(Long userId) {
//        Optional<WeightLog> latestWeight = weightLogRepository
//                .findFirstByUserIdOrderByLogDateDesc(userId);
//        return latestWeight.map(WeightLog::getWeight).orElse(null);
//    }
//}