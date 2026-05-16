package com.techtraining.evaluationservice.service;

import com.techtraining.evaluationservice.dto.request.AssignmentRequest;
import com.techtraining.evaluationservice.dto.response.AssignmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssignmentService {
    AssignmentResponse createAssignment(AssignmentRequest request);
    AssignmentResponse getAssignmentById(Long id);
    Page<AssignmentResponse> getAllAssignments(Pageable pageable);
    List<AssignmentResponse> getMyAssignments(Long evaluatorId);
    List<AssignmentResponse> getAssignmentsByEnrollment(Long enrollmentId);
    AssignmentResponse reassignEvaluator(Long id, Long evaluatorId);
    void deleteAssignment(Long id);
    void deleteAssignmentsByEnrollmentId(Long enrollmentId);
}
