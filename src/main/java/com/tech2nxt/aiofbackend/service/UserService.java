package com.tech2nxt.aiofbackend.service;


import com.tech2nxt.aiofbackend.dto.request.*;
import com.tech2nxt.aiofbackend.dto.response.*;
import com.tech2nxt.aiofbackend.exception.*;
import com.tech2nxt.aiofbackend.model.User;
import com.tech2nxt.aiofbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Get user profile by ID
     */
    @Transactional(readOnly = true)
    public UserResponse getUserProfile(Long userId) {
        log.info("Fetching profile for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return UserResponse.fromEntity(user);
    }

    /**
     * Update user profile
     */
    @Transactional
    public UserResponse updateUserProfile(Long userId, UpdateProfileRequest request) {
        log.info("Updating profile for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Update fields if provided
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getGender() != null) {
            user.setGender(User.Gender.valueOf(request.getGender().toUpperCase()));
        }
        if (request.getAge() != null) {
            user.setAge(request.getAge());
        }
        if (request.getFitnessGoal() != null) {
            user.setFitnessGoal(request.getFitnessGoal());
        }
        if (request.getExperienceLevel() != null) {
            user.setExperienceLevel(request.getExperienceLevel());
        }

        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user ID: {}", userId);

        return UserResponse.fromEntity(updatedUser);
    }

    /**
     * Deactivate user account
     */
    @Transactional
    public void deactivateAccount(Long userId) {
        log.info("Deactivating account for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setActive(false);
        userRepository.save(user);

        log.info("Account deactivated successfully for user ID: {}", userId);
    }
}
