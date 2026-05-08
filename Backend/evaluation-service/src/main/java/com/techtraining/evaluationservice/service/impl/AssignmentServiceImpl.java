package com.techtraining.evaluationservice.service.impl;

import com.techtraining.common.constants.AppConstants;
import com.techtraining.common.exception.DuplicateResourceException;
import com.techtraining.common.exception.ResourceNotFoundException;
import com.techtraining.evaluationservice.client.ParticipantClient;
import com.techtraining.evaluationservice.client.UserClient;
import com.techtraining.evaluationservice.dto.request.AssignmentRequest;
import com.techtraining.evaluationservice.dto.response.AssignmentResponse;
import com.techtraining.evaluationservice.entity.EvaluationAssignment;
import com.techtraining.evaluationservice.mapper.AssignmentMapper;
import com.techtraining.evaluationservice.repository.AssignmentRepository;
import com.techtraining.evaluationservice.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentMapper assignmentMapper;
    private final UserClient userClient;
    private final ParticipantClient participantClient;

    @Override
    @Transactional
    public AssignmentResponse createAssignment(AssignmentRequest request) {
        if (assignmentRepository.existsByEnrollmentIdAndRoundNumber(request.getEnrollmentId(), request.getRoundNumber())) {
            throw new DuplicateResourceException("Assignment already exists for this enrollment and round.");
        }

        // Validate evaluator
        try {

            Map<String, Object> response =
                    userClient.getUserById(
                            request.getEvaluatorId()
                    );

            if (response == null || response.get("data") == null) {

                throw new ResourceNotFoundException(
                        AppConstants.USER_NOT_FOUND
                                + request.getEvaluatorId()
                );
            }

        } catch (Exception e) {

            throw new ResourceNotFoundException(
                    AppConstants.USER_NOT_FOUND
                            + request.getEvaluatorId()
            );
        }

        // Validate enrollment
        try {

            Map<String, Object> response =
                    participantClient.getEnrollmentById(
                            request.getEnrollmentId()
                    );

            if (response == null || response.get("data") == null) {

                throw new ResourceNotFoundException(
                        "Enrollment not found with id: "
                                + request.getEnrollmentId()
                );
            }

        } catch (Exception e) {

            throw new ResourceNotFoundException(
                    "Enrollment not found with id: "
                            + request.getEnrollmentId()
            );
        }

        EvaluationAssignment assignment = assignmentMapper.toEntity(request);
        EvaluationAssignment saved = assignmentRepository.save(assignment);
        return assignmentMapper.toResponse(saved);
    }

    @Override
    public AssignmentResponse getAssignmentById(Long id) {
        EvaluationAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ASSIGNMENT_NOT_FOUND + id));
        return assignmentMapper.toResponse(assignment);
    }

    @Override
    public Page<AssignmentResponse> getAllAssignments(Pageable pageable) {
        return assignmentRepository.findAll(pageable).map(assignmentMapper::toResponse);
    }

    @Override
    public List<AssignmentResponse> getMyAssignments(Long evaluatorId) {
        List<EvaluationAssignment> assignments = assignmentRepository.findByEvaluatorId(evaluatorId);
        return assignments.stream().map(assignmentMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<AssignmentResponse> getAssignmentsByEnrollment(Long enrollmentId) {
        return assignmentMapper.toResponseList(assignmentRepository.findByEnrollmentId(enrollmentId));
    }

    @Override
    @Transactional
    public AssignmentResponse reassignEvaluator(Long id, Long evaluatorId) {
        EvaluationAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ASSIGNMENT_NOT_FOUND + id));
        
        // Validate new evaluator
        try {
            userClient.getUserById(evaluatorId);
        } catch (Exception e) {

            e.printStackTrace();

            throw new ResourceNotFoundException(
                    "USER ERROR = " + e.getMessage()
            );
        }

        assignment.setEvaluatorId(evaluatorId);
        EvaluationAssignment updated = assignmentRepository.save(assignment);
        return assignmentMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteAssignment(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new ResourceNotFoundException(AppConstants.ASSIGNMENT_NOT_FOUND + id);
        }
        assignmentRepository.deleteById(id);
    }
}
