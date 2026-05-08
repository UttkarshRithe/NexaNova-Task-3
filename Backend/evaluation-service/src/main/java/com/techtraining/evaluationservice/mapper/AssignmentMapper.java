package com.techtraining.evaluationservice.mapper;

import com.techtraining.evaluationservice.dto.request.AssignmentRequest;
import com.techtraining.evaluationservice.dto.response.AssignmentResponse;
import com.techtraining.evaluationservice.entity.EvaluationAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface AssignmentMapper {
    @Mapping(target = "evaluatorName", ignore = true)
    AssignmentResponse toResponse(EvaluationAssignment assignment);
    
    @Mapping(target = "status", constant = "PENDING")
    EvaluationAssignment toEntity(AssignmentRequest request);
    
    List<AssignmentResponse> toResponseList(List<EvaluationAssignment> assignments);
}
