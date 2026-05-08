package com.techtraining.evaluationservice.mapper;

import com.techtraining.evaluationservice.dto.request.ResultRequest;
import com.techtraining.evaluationservice.dto.response.ResultResponse;
import com.techtraining.evaluationservice.entity.EvaluationResult;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-08T23:01:51+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Microsoft)"
)
public class ResultMapperImpl implements ResultMapper {

    @Override
    public ResultResponse toResponse(EvaluationResult result) {
        if ( result == null ) {
            return null;
        }

        ResultResponse resultResponse = new ResultResponse();

        resultResponse.setId( result.getId() );
        resultResponse.setAssignmentId( result.getAssignmentId() );
        resultResponse.setScore( result.getScore() );
        resultResponse.setComments( result.getComments() );
        resultResponse.setSubmittedAt( result.getSubmittedAt() );

        return resultResponse;
    }

    @Override
    public EvaluationResult toEntity(ResultRequest request) {
        if ( request == null ) {
            return null;
        }

        EvaluationResult.EvaluationResultBuilder evaluationResult = EvaluationResult.builder();

        evaluationResult.assignmentId( request.getAssignmentId() );
        evaluationResult.score( request.getScore() );
        evaluationResult.comments( request.getComments() );

        return evaluationResult.build();
    }

    @Override
    public List<ResultResponse> toResponseList(List<EvaluationResult> results) {
        if ( results == null ) {
            return null;
        }

        List<ResultResponse> list = new ArrayList<ResultResponse>( results.size() );
        for ( EvaluationResult evaluationResult : results ) {
            list.add( toResponse( evaluationResult ) );
        }

        return list;
    }
}
