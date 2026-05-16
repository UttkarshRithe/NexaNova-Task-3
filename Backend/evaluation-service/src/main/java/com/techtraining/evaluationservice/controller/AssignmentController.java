package com.techtraining.evaluationservice.controller;

import com.techtraining.common.constants.AppConstants;
import com.techtraining.common.dto.ApiResponse;
import com.techtraining.evaluationservice.dto.request.AssignmentRequest;
import com.techtraining.evaluationservice.dto.response.AssignmentResponse;
import com.techtraining.evaluationservice.service.AssignmentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluation-assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @Value("${internal.secret:nexanova-internal-secret}")
    private String internalSecret;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AssignmentResponse>> createAssignment(@Valid @RequestBody AssignmentRequest request) {
        AssignmentResponse response = assignmentService.createAssignment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(AppConstants.ASSIGNMENT_CREATED, response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssignmentResponse>> getAssignment(@PathVariable Long id) {
        AssignmentResponse response = assignmentService.getAssignmentById(id);
        return ResponseEntity.ok(ApiResponse.success("Assignment fetched successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<AssignmentResponse>>> getAllAssignments(Pageable pageable) {
        Page<AssignmentResponse> response = assignmentService.getAllAssignments(pageable);
        return ResponseEntity.ok(ApiResponse.success("Assignments fetched successfully", response));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('EVALUATOR')")
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getMyAssignments(HttpServletRequest request) {
        Long evaluatorId = Long.parseLong(request.getHeader("X-User-Id"));
        List<AssignmentResponse> response = assignmentService.getMyAssignments(evaluatorId);
        return ResponseEntity.ok(ApiResponse.success("My assignments fetched successfully", response));
    }

    @GetMapping("/enrollment/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getAssignmentsByEnrollment(@PathVariable Long id) {
        List<AssignmentResponse> response = assignmentService.getAssignmentsByEnrollment(id);
        return ResponseEntity.ok(ApiResponse.success("Assignments for enrollment fetched successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AssignmentResponse>> reassignEvaluator(@PathVariable Long id, @RequestParam Long evaluatorId) {
        AssignmentResponse response = assignmentService.reassignEvaluator(id, evaluatorId);
        return ResponseEntity.ok(ApiResponse.success("Evaluator reassigned successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.ok(ApiResponse.success("Assignment removed successfully", null));
    }

    // ✅ FIX: Secured internal endpoint — requires X-Internal-Secret header.
    // This path should also be blocked at the API Gateway for external callers.
    @DeleteMapping("/internal/enrollment/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAssignmentsByEnrollmentInternal(
            @PathVariable Long id,
            @RequestHeader(value = "X-Internal-Secret", required = false) String secret) {

        if (!internalSecret.equals(secret)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.success("Access denied", null));
        }
        assignmentService.deleteAssignmentsByEnrollmentId(id);
        return ResponseEntity.ok(ApiResponse.success("Assignments removed for enrollment", null));
    }
}
