package com.techtraining.evaluationservice.repository;

import com.techtraining.evaluationservice.entity.EvaluationAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<EvaluationAssignment, Long> {
    List<EvaluationAssignment> findByEvaluatorId(Long evaluatorId);
    List<EvaluationAssignment> findByEnrollmentId(Long enrollmentId);
    boolean existsByEnrollmentIdAndRoundNumber(Long enrollmentId, Integer roundNumber);
}
