package com.tech2nxt.aiofbackend.controller;


import com.tech2nxt.aiofbackend.dto.ApiResponse;
import com.tech2nxt.aiofbackend.dto.request.UpdateProfileRequest;
import com.tech2nxt.aiofbackend.dto.response.*;
import com.tech2nxt.aiofbackend.security.UserPrincipal;
import com.tech2nxt.aiofbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Profile", description = "User profile management endpoints")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(
            summary = "Get user profile",
            description = "Retrieve authenticated user's profile information"
    )
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("GET /api/users/profile - User ID: {}", userPrincipal.getId());

        UserResponse response = userService.getUserProfile(userPrincipal.getId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/profile")
    @Operation(
            summary = "Update user profile",
            description = "Update authenticated user's profile information"
    )
    public ResponseEntity<ApiResponse<UserResponse>> updateUserProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateProfileRequest request) {
        log.info("PUT /api/users/profile - User ID: {}", userPrincipal.getId());

        UserResponse response = userService.updateUserProfile(userPrincipal.getId(), request);

        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    @DeleteMapping("/account")
    @Operation(
            summary = "Deactivate account",
            description = "Deactivate authenticated user's account (soft delete)"
    )
    public ResponseEntity<ApiResponse<Void>> deactivateAccount(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("DELETE /api/users/account - User ID: {}", userPrincipal.getId());

        userService.deactivateAccount(userPrincipal.getId());

        return ResponseEntity.ok(ApiResponse.success("Account deactivated successfully"));
    }
}
