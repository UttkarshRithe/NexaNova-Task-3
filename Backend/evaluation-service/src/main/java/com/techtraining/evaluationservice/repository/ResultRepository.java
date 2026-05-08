package com.techtraining.evaluationservice.repository;

import com.techtraining.evaluationservice.entity.EvaluationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<EvaluationResult, Long> {
    Optional<EvaluationResult> findByAssignmentId(Long assignmentId);
}
