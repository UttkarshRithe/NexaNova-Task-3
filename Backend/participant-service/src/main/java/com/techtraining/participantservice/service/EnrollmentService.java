package com.techtraining.participantservice.service;

import com.techtraining.participantservice.dto.request.EnrollmentRequest;
import com.techtraining.participantservice.dto.response.EnrollmentResponse;

import java.util.List;

public interface EnrollmentService {
    EnrollmentResponse enrollParticipant(EnrollmentRequest request);
    EnrollmentResponse getEnrollmentById(Long id);
    List<EnrollmentResponse> getAllEnrollments();
    List<EnrollmentResponse> getEnrollmentsByParticipantId(Long participantId);
    List<EnrollmentResponse> getEnrollmentsByBatchTechnologyId(Long btId);
    void deleteEnrollment(Long id);
}
