package com.techtraining.evaluationservice.service.impl;

import com.techtraining.common.constants.AppConstants;
import com.techtraining.common.exception.DuplicateResourceException;
import com.techtraining.common.exception.ForbiddenException;
import com.techtraining.common.exception.ResourceNotFoundException;
import com.techtraining.evaluationservice.dto.request.ResultRequest;
import com.techtraining.evaluationservice.dto.response.ResultResponse;
import com.techtraining.evaluationservice.entity.EvaluationAssignment;
import com.techtraining.evaluationservice.entity.EvaluationResult;
import com.techtraining.evaluationservice.enums.AssignmentStatus;
import com.techtraining.evaluationservice.mapper.ResultMapper;
import com.techtraining.evaluationservice.repository.AssignmentRepository;
import com.techtraining.evaluationservice.repository.ResultRepository;
import com.techtraining.evaluationservice.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {

    private final ResultRepository resultRepository;
    private final AssignmentRepository assignmentRepository;
    private final ResultMapper resultMapper;

    @Override
    @Transactional
    public ResultResponse submitResult(ResultRequest request, Long evaluatorId) {
        // 1. Find the assignment
        EvaluationAssignment assignment = assignmentRepository
            .findById(request.getAssignmentId())
            .orElseThrow(() -> new ResourceNotFoundException(
                AppConstants.ASSIGNMENT_NOT_FOUND + request.getAssignmentId()));

        // 2. Verify this evaluator owns this assignment
        if (!assignment.getEvaluatorId().equals(evaluatorId)) {
            throw new ForbiddenException(AppConstants.EVAL_NOT_ASSIGNED);
        }

        // 3. Prevent duplicate submission
        if (assignment.getStatus() == AssignmentStatus.COMPLETED) {
            throw new DuplicateResourceException(AppConstants.EVAL_ALREADY_DONE);
        }

        // 4. Save result — Operation 1
        EvaluationResult result = EvaluationResult.builder()
            .assignmentId(assignment.getId())
            .score(request.getScore())
            .comments(request.getComments())
            .build();
        EvaluationResult saved = resultRepository.save(result);

        // 5. Update assignment status — Operation 2
        assignment.setStatus(AssignmentStatus.COMPLETED);
        assignmentRepository.save(assignment);

        // @Transactional ensures both succeed or both rollback
        return resultMapper.toResponse(saved);
    }

    @Override
    public ResultResponse getResultById(Long id) {
        EvaluationResult result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation result not found with id: " + id));
        return resultMapper.toResponse(result);
    }

    @Override
    @Transactional
    public ResultResponse updateResult(Long id, ResultRequest request, Long evaluatorId) {
        EvaluationResult result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation result not found with id: " + id));
        
        EvaluationAssignment assignment = assignmentRepository.findById(result.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ASSIGNMENT_NOT_FOUND + result.getAssignmentId()));

        if (!assignment.getEvaluatorId().equals(evaluatorId)) {
            throw new ForbiddenException(AppConstants.EVAL_NOT_ASSIGNED);
        }

        result.setScore(request.getScore());
        result.setComments(request.getComments());
        
        EvaluationResult updated = resultRepository.save(result);
        return resultMapper.toResponse(updated);
    }

    @Override
    public ResultResponse getResultByAssignment(Long assignmentId) {
        EvaluationResult result = resultRepository.findByAssignmentId(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found for assignment id: " + assignmentId));
        return resultMapper.toResponse(result);
    }
}
