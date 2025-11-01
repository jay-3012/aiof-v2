package com.tech2nxt.aiofbackend.service;


import com.tech2nxt.aiofbackend.dto.request.*;
import com.tech2nxt.aiofbackend.dto.response.*;
import com.tech2nxt.aiofbackend.exception.*;
import com.tech2nxt.aiofbackend.model.User;
import com.tech2nxt.aiofbackend.repository.UserRepository;
import com.tech2nxt.aiofbackend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Register new user
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email already exists - {}", request.getEmail());
            throw new ConflictException("Email already registered");
        }

        // Create user entity
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender() != null ? User.Gender.valueOf(request.getGender().toUpperCase()) : null)
                .age(request.getAge())
                .fitnessGoal(request.getFitnessGoal())
                .experienceLevel(request.getExperienceLevel())
                .active(true)
                .build();

        // Save user
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(savedUser.getEmail(), savedUser.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.getEmail(), savedUser.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(UserResponse.fromEntity(savedUser))
                .build();
    }

    /**
     * Login user
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting login for email: {}", request.getEmail());

        // Find user by email
        User user = userRepository.findByEmailAndActive(request.getEmail(), true)
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found or inactive - {}", request.getEmail());
                    return new BadCredentialsException("Invalid email or password");
                });

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Login failed: Invalid password for email - {}", request.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }

        log.info("User logged in successfully: {}", user.getId());

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail(), user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(UserResponse.fromEntity(user))
                .build();
    }

    /**
     * Refresh access token
     */
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Attempting to refresh token");

        // Validate refresh token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.warn("Token refresh failed: Invalid refresh token");
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        // Extract user info from token
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // Verify user still exists and is active
        User user = userRepository.findByEmailAndActive(email, true)
                .orElseThrow(() -> {
                    log.warn("Token refresh failed: User not found or inactive - {}", email);
                    return new UnauthorizedException("User not found or inactive");
                });

        log.info("Token refreshed successfully for user: {}", userId);

        // Generate new tokens
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getId());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail(), user.getId());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .user(UserResponse.fromEntity(user))
                .build();
    }
}
