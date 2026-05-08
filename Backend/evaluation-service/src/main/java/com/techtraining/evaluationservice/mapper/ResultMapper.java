package com.techtraining.evaluationservice.mapper;

import com.techtraining.evaluationservice.dto.request.ResultRequest;
import com.techtraining.evaluationservice.dto.response.ResultResponse;
import com.techtraining.evaluationservice.entity.EvaluationResult;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ResultMapper {
    ResultResponse toResponse(EvaluationResult result);
    EvaluationResult toEntity(ResultRequest request);
    List<ResultResponse> toResponseList(List<EvaluationResult> results);
}
