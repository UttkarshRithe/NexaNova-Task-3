package com.techtraining.evaluationservice.service;

import com.techtraining.evaluationservice.dto.request.ResultRequest;
import com.techtraining.evaluationservice.dto.response.ResultResponse;

public interface ResultService {
    ResultResponse submitResult(ResultRequest request, Long evaluatorId);
    ResultResponse getResultById(Long id);
    ResultResponse updateResult(Long id, ResultRequest request, Long evaluatorId);
    ResultResponse getResultByAssignment(Long assignmentId);
}
