package com.techtraining.participantservice.controller;

import com.techtraining.common.constants.AppConstants;
import com.techtraining.common.dto.ApiResponse;
import com.techtraining.participantservice.dto.request.ParticipantRequest;
import com.techtraining.participantservice.dto.response.ParticipantResponse;
import com.techtraining.participantservice.service.ParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/participants")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ParticipantResponse>> addParticipant(@Valid @RequestBody ParticipantRequest request) {
        ParticipantResponse response = participantService.addParticipant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(AppConstants.PARTICIPANT_CREATED, response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ParticipantResponse>> getParticipant(@PathVariable Long id) {
        ParticipantResponse response = participantService.getParticipantById(id);
        return ResponseEntity.ok(ApiResponse.success("Participant fetched successfully", response));
    }

    // Internal API for service-to-service lookup
    @GetMapping("/internal/{id}")
    public ParticipantResponse getParticipantInternal(@PathVariable Long id) {
        return participantService.getParticipantById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ParticipantResponse>>> getAllParticipants(Pageable pageable) {
        Page<ParticipantResponse> response = participantService.getAllParticipants(pageable);
        return ResponseEntity.ok(ApiResponse.success("Participants fetched successfully", response));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<java.util.List<ParticipantResponse>>> getAllParticipants() {
        java.util.List<ParticipantResponse> response = participantService.getAllParticipants();
        return ResponseEntity.ok(ApiResponse.success("All participants fetched successfully", response));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ParticipantResponse>> updateParticipant(@PathVariable Long id, @Valid @RequestBody ParticipantRequest request) {
        ParticipantResponse response = participantService.updateParticipant(id, request);
        return ResponseEntity.ok(ApiResponse.success("Participant updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteParticipant(@PathVariable Long id) {
        participantService.deleteParticipant(id);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.PARTICIPANT_DELETED, null));
    }
}
