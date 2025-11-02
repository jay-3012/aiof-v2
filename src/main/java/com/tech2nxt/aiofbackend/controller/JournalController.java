package com.tech2nxt.aiofbackend.controller;

import com.tech2nxt.aiofbackend.dto.ApiResponse;
import com.tech2nxt.aiofbackend.dto.request.CreateJournalRequest;
import com.tech2nxt.aiofbackend.dto.response.JournalEntryResponse;
import com.tech2nxt.aiofbackend.security.UserPrincipal;
import com.tech2nxt.aiofbackend.service.JournalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/journals")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Journals", description = "Workout journal entries with images")
public class JournalController {

    private final JournalService journalService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Create journal entry",
            description = "Create a journal entry with image and description after completing a workout"
    )
    public ResponseEntity<ApiResponse<JournalEntryResponse>> createJournal(
            @AuthenticationPrincipal UserPrincipal userPrincipal,

            @Parameter(description = "Session ID", required = true)
            @RequestParam Long sessionId,

            @Parameter(description = "Journal description (10-5000 characters)", required = true)
            @RequestParam String description,

            @Parameter(description = "Journal image (max 5MB, JPG/PNG/WebP)",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam(required = false) MultipartFile image) {

        log.info("POST /api/journals - User ID: {}, Session ID: {}", userPrincipal.getId(), sessionId);

        CreateJournalRequest request = CreateJournalRequest.builder()
                .sessionId(sessionId)
                .description(description)
                .build();

        JournalEntryResponse response = journalService.createJournal(userPrincipal.getId(), request, image);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Journal created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get journal by ID",
            description = "Retrieve a specific journal entry"
    )
    public ResponseEntity<ApiResponse<JournalEntryResponse>> getJournalById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Journal ID") @PathVariable Long id) {

        log.info("GET /api/journals/{} - User ID: {}", id, userPrincipal.getId());

        JournalEntryResponse response = journalService.getJournalById(id, userPrincipal.getId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/session/{sessionId}")
    @Operation(
            summary = "Get journal by session",
            description = "Retrieve journal entry for a specific workout session"
    )
    public ResponseEntity<ApiResponse<JournalEntryResponse>> getJournalBySession(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Session ID") @PathVariable Long sessionId) {

        log.info("GET /api/journals/session/{} - User ID: {}", sessionId, userPrincipal.getId());

        JournalEntryResponse response = journalService.getJournalBySession(sessionId, userPrincipal.getId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(
            summary = "Get all journals",
            description = "Retrieve paginated list of journal entries"
    )
    public ResponseEntity<ApiResponse<Page<JournalEntryResponse>>> getAllJournals(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") Integer pageSize) {

        log.info("GET /api/journals - User ID: {}, page: {}", userPrincipal.getId(), page);

        Page<JournalEntryResponse> response = journalService.getAllJournals(
                userPrincipal.getId(), page, pageSize);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Update journal entry",
            description = "Update journal description and/or image"
    )
    public ResponseEntity<ApiResponse<JournalEntryResponse>> updateJournal(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Journal ID") @PathVariable Long id,

            @Parameter(description = "New journal description")
            @RequestParam(required = false) String description,

            @Parameter(description = "New journal image",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam(required = false) MultipartFile image) {

        log.info("PUT /api/journals/{} - User ID: {}", id, userPrincipal.getId());

        JournalEntryResponse response = journalService.updateJournal(
                id, userPrincipal.getId(), description, image);

        return ResponseEntity.ok(ApiResponse.success("Journal updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete journal entry",
            description = "Delete a journal entry and its associated image"
    )
    public ResponseEntity<ApiResponse<Void>> deleteJournal(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Journal ID") @PathVariable Long id) {

        log.info("DELETE /api/journals/{} - User ID: {}", id, userPrincipal.getId());

        journalService.deleteJournal(id, userPrincipal.getId());

        return ResponseEntity.ok(ApiResponse.success("Journal deleted successfully"));
    }
}