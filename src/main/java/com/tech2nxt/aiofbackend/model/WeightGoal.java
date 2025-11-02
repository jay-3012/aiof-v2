package com.tech2nxt.aiofbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "weight_goals")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeightGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal startingWeight;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal targetWeight;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GoalType goalType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GoalStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum GoalType {
        WEIGHT_LOSS,
        WEIGHT_GAIN,
        MAINTENANCE
    }

    public enum GoalStatus {
        ACTIVE,
        COMPLETED,
        ABANDONED
    }

    /**
     * Calculate progress percentage
     */
    public BigDecimal calculateProgress(BigDecimal currentWeight) {
        if (currentWeight == null) return BigDecimal.ZERO;

        BigDecimal totalChange = targetWeight.subtract(startingWeight);
        if (totalChange.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        BigDecimal currentChange = currentWeight.subtract(startingWeight);
        return currentChange.divide(totalChange, 2, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Check if goal is achieved
     */
    public boolean isAchieved(BigDecimal currentWeight) {
        if (currentWeight == null) return false;

        if (goalType == GoalType.WEIGHT_LOSS) {
            return currentWeight.compareTo(targetWeight) <= 0;
        } else if (goalType == GoalType.WEIGHT_GAIN) {
            return currentWeight.compareTo(targetWeight) >= 0;
        }
        return false;
    }
}