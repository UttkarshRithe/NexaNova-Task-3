package com.techtraining.participantservice.service.impl;

import com.techtraining.common.dto.ApiResponse;
import com.techtraining.common.exception.DuplicateResourceException;
import com.techtraining.common.exception.ResourceNotFoundException;
import com.techtraining.participantservice.client.BatchClient;
import com.techtraining.participantservice.client.EvaluationClient;
import com.techtraining.participantservice.dto.request.EnrollmentRequest;
import com.techtraining.participantservice.dto.response.EnrollmentResponse;
import com.techtraining.participantservice.entity.Enrollment;
import com.techtraining.participantservice.entity.Participant;
import com.techtraining.participantservice.mapper.EnrollmentMapper;
import com.techtraining.participantservice.repository.EnrollmentRepository;
import com.techtraining.participantservice.repository.ParticipantRepository;
import com.techtraining.participantservice.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final ParticipantRepository participantRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final BatchClient batchClient;
    private final EvaluationClient evaluationClient;
    private final RabbitTemplate rabbitTemplate;

    @Value("${internal.secret:nexanova-internal-secret}")
    private String internalSecret;

    @Override
    @Transactional
    public EnrollmentResponse enrollParticipant(EnrollmentRequest request) {

        if (enrollmentRepository.existsByParticipantIdAndBatchTechnologyId(
                request.getParticipantId(),
                request.getBatchTechnologyId()
        )) {

            throw new DuplicateResourceException(
                    "Participant is already enrolled in this batch-technology."
            );
        }

        Participant participant = participantRepository.findById(
                        request.getParticipantId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Participant not found with id: "
                                        + request.getParticipantId()
                        )
                );

        // ✅ Validate batchTechnologyId via Feign call
        try {

            Map<String, Object> response =
                    batchClient.getBatchTechnology(
                            request.getBatchTechnologyId()
                    );

            if (response == null || response.get("data") == null) {

                throw new ResourceNotFoundException(
                        "Invalid Batch-Technology ID: "
                                + request.getBatchTechnologyId()
                );

            }

        } catch (Exception e) {

            e.printStackTrace();

            throw new ResourceNotFoundException(
                    "Invalid Batch-Technology ID: "
                            + request.getBatchTechnologyId()
                            + " ERROR = "
                            + e.getMessage()
            );
        }

        Enrollment enrollment = Enrollment.builder()
                .participant(participant)
                .batchTechnologyId(request.getBatchTechnologyId())
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);

        // Publish enrollment event
        try {
            rabbitTemplate.convertAndSend(
                "evaltrack.exchange",
                "enrollment.created",
                saved
            );
            log.info("Published enrollment created event for participant {}", saved.getParticipant().getId());
        } catch (Exception e) {
            log.error("Failed to publish enrollment event: {}", e.getMessage());
        }

        return enrollmentMapper.toResponse(saved);
    }

    @Override
    public EnrollmentResponse getEnrollmentById(Long id) {

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Enrollment not found with id: " + id
                        )
                );

        return enrollmentMapper.toResponse(enrollment);
    }

    @Override
    public List<EnrollmentResponse> getAllEnrollments() {

        return enrollmentMapper.toResponseList(
                enrollmentRepository.findAll()
        );
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByParticipantId(
            Long participantId
    ) {

        return enrollmentMapper.toResponseList(
                enrollmentRepository.findByParticipantId(participantId)
        );
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByBatchTechnologyId(
            Long btId
    ) {

        return enrollmentMapper.toResponseList(
                enrollmentRepository.findByBatchTechnologyId(btId)
        );
    }

    @Override
    @Transactional
    public void deleteEnrollment(Long id) {

        if (!enrollmentRepository.existsById(id)) {

            throw new ResourceNotFoundException(
                    "Enrollment not found with id: " + id
            );
        }

        // ✅ FIX: Wrap Feign call in try/catch.
        // A failed cascade must NOT block enrollment deletion.
        // Any surviving orphan assignments will be cleaned up by the
        // scheduled OrphanAssignmentCleanupJob in evaluation-service.
        try {
            evaluationClient.deleteAssignmentsByEnrollment(id, internalSecret);
        } catch (Exception e) {
            log.error(
                "[EnrollmentService] Failed to cascade-delete assignments for enrollment={}: {}",
                id, e.getMessage()
            );
            // Do NOT rethrow — enrollment deletion must still proceed.
        }

        enrollmentRepository.deleteById(id);
        log.info("[EnrollmentService] Enrollment {} deleted successfully.", id);
    }
}