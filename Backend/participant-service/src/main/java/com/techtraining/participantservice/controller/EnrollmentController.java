package com.techtraining.participantservice.controller;

import com.techtraining.common.constants.AppConstants;
import com.techtraining.common.dto.ApiResponse;
import com.techtraining.participantservice.dto.request.EnrollmentRequest;
import com.techtraining.participantservice.dto.response.EnrollmentResponse;
import com.techtraining.participantservice.dto.response.ParticipantResponse;
import com.techtraining.participantservice.service.EnrollmentService;
import com.techtraining.participantservice.service.ParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final ParticipantService participantService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollParticipant(@Valid @RequestBody EnrollmentRequest request) {
        EnrollmentResponse response = enrollmentService.enrollParticipant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(AppConstants.ENROLLMENT_CREATED, response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> getEnrollment(@PathVariable Long id) {
        EnrollmentResponse response = enrollmentService.getEnrollmentById(id);
        return ResponseEntity.ok(ApiResponse.success("Enrollment fetched successfully", response));
    }

    // Internal API for service-to-service lookup
    @GetMapping("/internal/{id}")
    public EnrollmentResponse getEnrollmentInternal(@PathVariable Long id) {
        return enrollmentService.getEnrollmentById(id);
    }

    @GetMapping("/internal/email-by-enrollment/{id}")
    public String getParticipantEmailByEnrollmentId(@PathVariable Long id) {
        EnrollmentResponse enrollment = enrollmentService.getEnrollmentById(id);
        if (enrollment != null && enrollment.getParticipantId() != null) {
            ParticipantResponse participant = participantService.getParticipantById(enrollment.getParticipantId());
            return participant != null ? participant.getEmail() : null;
        }
        return null;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getAllEnrollments() {
        List<EnrollmentResponse> response = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(ApiResponse.success("Enrollments fetched successfully", response));
    }

    @GetMapping("/participant/{participantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getEnrollmentsByParticipant(@PathVariable Long participantId) {
        List<EnrollmentResponse> response = enrollmentService.getEnrollmentsByParticipantId(participantId);
        return ResponseEntity.ok(ApiResponse.success("Enrollments for participant fetched successfully", response));
    }

    @GetMapping("/batch-technology/{btId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getEnrollmentsByBatchTechnology(@PathVariable Long btId) {
        List<EnrollmentResponse> response = enrollmentService.getEnrollmentsByBatchTechnologyId(btId);
        return ResponseEntity.ok(ApiResponse.success("Enrollments for batch-technology fetched successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.ENROLLMENT_DELETED, null));
    }
}
