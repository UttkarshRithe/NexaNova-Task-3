package com.techtraining.evaluationservice.scheduler;

import com.techtraining.evaluationservice.client.ParticipantClient;
import com.techtraining.evaluationservice.entity.EvaluationAssignment;
import com.techtraining.evaluationservice.repository.AssignmentRepository;
import com.techtraining.evaluationservice.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrphanAssignmentCleanupJob {

    private final AssignmentRepository assignmentRepository;
    private final ResultRepository resultRepository;
    private final ParticipantClient participantClient;

    @Scheduled(cron = "0 0 2 * * *") // Run daily at 2am
    @Transactional
    public void cleanupOrphanAssignments() {
        log.info("[CleanupJob] Starting orphan assignment scan...");
        List<EvaluationAssignment> all = assignmentRepository.findAll();
        List<Long> orphanIds = new ArrayList<>();

        for (EvaluationAssignment assignment : all) {
            try {
                Object enrollment = participantClient.getEnrollmentById(assignment.getEnrollmentId());
                if (enrollment == null) {
                    orphanIds.add(assignment.getId());
                }
            } catch (Exception e) {
                // Enrollment deleted or not found
                orphanIds.add(assignment.getId());
            }
        }

        if (!orphanIds.isEmpty()) {
            log.warn("[CleanupJob] Found {} orphan assignments. Deleting...", orphanIds.size());
            resultRepository.deleteByAssignmentIdIn(orphanIds);
            assignmentRepository.deleteAllById(orphanIds);
            log.info("[CleanupJob] Successfully deleted orphan assignments.");
        } else {
            log.info("[CleanupJob] No orphan assignments found.");
        }
    }
}
