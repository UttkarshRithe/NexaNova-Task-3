package com.techtraining.evaluationservice.service.impl;

import com.techtraining.common.constants.AppConstants;
import com.techtraining.common.exception.DuplicateResourceException;
import com.techtraining.common.exception.ResourceNotFoundException;
import com.techtraining.evaluationservice.client.ParticipantClient;
import com.techtraining.evaluationservice.client.UserClient;
import com.techtraining.evaluationservice.client.BatchClient;
import com.techtraining.evaluationservice.dto.request.AssignmentRequest;
import com.techtraining.evaluationservice.dto.response.AssignmentResponse;
import com.techtraining.evaluationservice.entity.EvaluationAssignment;
import com.techtraining.evaluationservice.mapper.AssignmentMapper;
import com.techtraining.evaluationservice.repository.AssignmentRepository;
import com.techtraining.evaluationservice.repository.ResultRepository;
import com.techtraining.common.event.EvaluationAssignedEvent;
import com.techtraining.evaluationservice.producer.EvaluationAssignmentProducer;
import com.techtraining.evaluationservice.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentMapper assignmentMapper;
    private final UserClient userClient;
    private final ParticipantClient participantClient;
    private final BatchClient batchClient;
    private final ResultRepository resultRepository;
    private final EvaluationAssignmentProducer evaluationAssignmentProducer;

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

            if (response == null || response.get("id") == null) {

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

            Object nestedData = response != null ? response.get("data") : null;
            Object rootId = response != null ? response.get("id") : null;
            if (response == null || (nestedData == null && rootId == null)) {

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
        AssignmentResponse response = enrichWithEvaluatorName(assignmentMapper.toResponse(saved));

        // Publish event for asynchronous notification
        try {
            String participantEmail = fetchParticipantEmail(saved.getEnrollmentId());
            com.techtraining.common.event.EvaluationAssignedEvent event = 
                com.techtraining.common.event.EvaluationAssignedEvent.builder()
                    .assignmentId(saved.getId())
                    .participantName(response.getParticipantName())
                    .participantEmail(participantEmail)
                    .batchName(response.getBatchName())
                    .technologyName(response.getTechnologyName())
                    .roundNumber(saved.getRoundNumber())
                    .evaluatorName(response.getEvaluatorName())
                    .evaluationDate(java.time.LocalDate.now().plusDays(1))
                    .evaluationTime("10:00 AM")
                    .meetingLink("https://meet.google.com/abc-xyz")
                    .build();

            evaluationAssignmentProducer.publishEvaluationAssignedEvent(event);
        } catch (Exception e) {
            log.error("[ASSIGNMENT-EVENT-ERROR] Failed to publish event for assignment: {}", saved.getId(), e);
        }

        return response;
    }

    @Override
    public AssignmentResponse getAssignmentById(Long id) {
        EvaluationAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ASSIGNMENT_NOT_FOUND + id));
        return enrichWithEvaluatorName(assignmentMapper.toResponse(assignment));
    }

    @Override
    public Page<AssignmentResponse> getAllAssignments(Pageable pageable) {
        // We do NOT filter orphans here because filtering a Spring Data Page breaks pagination.
        // Instead, the frontend (EvaluationAssignmentList.tsx) filters out orphan assignments 
        // after fetching the page, and the OrphanAssignmentCleanupJob cleans them from the DB.
        return assignmentRepository.findAll(pageable)
                .map(assignmentMapper::toResponse)
                .map(this::enrichWithEvaluatorName);
    }

    @Override
    public List<AssignmentResponse> getMyAssignments(Long evaluatorId) {
        List<EvaluationAssignment> assignments = assignmentRepository.findByEvaluatorId(evaluatorId);
        return assignments.stream()
                .map(assignmentMapper::toResponse)
                .map(this::enrichWithEvaluatorName)
                // ✅ FIX: Exclude orphan assignments whose enrollment was deleted.
                // enrichWithEvaluatorName silently catches Feign 404 → participantName stays null.
                .filter(a -> a.getParticipantName() != null && !a.getParticipantName().isBlank())
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentResponse> getAssignmentsByEnrollment(Long enrollmentId) {
        return assignmentMapper.toResponseList(assignmentRepository.findByEnrollmentId(enrollmentId))
                .stream()
                .map(this::enrichWithEvaluatorName)
                .collect(Collectors.toList());
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
        return enrichWithEvaluatorName(assignmentMapper.toResponse(updated));
    }

    @Override
    @Transactional
    public void deleteAssignment(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new ResourceNotFoundException(AppConstants.ASSIGNMENT_NOT_FOUND + id);
        }
        assignmentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAssignmentsByEnrollmentId(Long enrollmentId) {
        List<EvaluationAssignment> assignments = assignmentRepository.findByEnrollmentId(enrollmentId);
        if (assignments.isEmpty()) {
            return;
        }

        List<Long> assignmentIds = assignments.stream()
                .map(EvaluationAssignment::getId)
                .toList();
        resultRepository.deleteByAssignmentIdIn(assignmentIds);
        assignmentRepository.deleteAllByEnrollmentId(enrollmentId);
    }

    private AssignmentResponse enrichWithEvaluatorName(AssignmentResponse response) {
        if (response.getEvaluatorId() == null) return response;
        try {
            Map<String, Object> userResponse = userClient.getUserById(response.getEvaluatorId());
            if (userResponse != null && userResponse.get("name") != null) {
                response.setEvaluatorName((String) userResponse.get("name"));
            }
        } catch (Exception e) {
            // Ignored
        }

        try {
            Map<String, Object> enrollmentResponse = participantClient.getEnrollmentById(response.getEnrollmentId());
            if (enrollmentResponse != null) {
                Object participantName = enrollmentResponse.get("participantName");
                Object batchTechnologyId = enrollmentResponse.get("batchTechnologyId");

                if (participantName != null) {
                    response.setParticipantName(String.valueOf(participantName));
                }

                if (batchTechnologyId instanceof Number btIdNumber) {
                    Map<String, Object> batchTechnologyResponse =
                            batchClient.getBatchTechnologyById(btIdNumber.longValue());

                    if (batchTechnologyResponse != null) {
                        Object batchName = batchTechnologyResponse.get("batchName");
                        Object technologyName = batchTechnologyResponse.get("technologyName");

                        if (batchName != null) {
                            response.setBatchName(String.valueOf(batchName));
                        }
                        if (technologyName != null) {
                            response.setTechnologyName(String.valueOf(technologyName));
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Enrollment not found — this assignment is an orphan.
            // participantName remains null, which callers use as an orphan signal.
            log.warn("[AssignmentService] Orphan detected: assignmentId={}, enrollmentId={} — enrollment no longer exists.",
                     response.getId(), response.getEnrollmentId());
        }
        return response;
    }

    private String fetchParticipantEmail(Long enrollmentId) {
        log.info("[AssignmentService] Attempting to fetch participant email for enrollmentId: {}", enrollmentId);
        try {
            String email = participantClient.getParticipantEmailByEnrollmentId(enrollmentId);
            if (email != null && !email.isEmpty()) {
                log.info("[AssignmentService] Successfully fetched email: {}", email);
                return email;
            }
        } catch (Exception e) {
            log.error("[AssignmentService] Error calling internal email lookup for enrollment: {}. Error: {}", 
                     enrollmentId, e.getMessage());
        }
        log.warn("[AssignmentService] Email lookup failed or returned empty, falling back to participant@example.com");
        return "participant@example.com";
    }
}
