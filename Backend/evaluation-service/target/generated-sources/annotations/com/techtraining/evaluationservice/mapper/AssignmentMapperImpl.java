package com.techtraining.evaluationservice.mapper;

import com.techtraining.evaluationservice.dto.request.AssignmentRequest;
import com.techtraining.evaluationservice.dto.response.AssignmentResponse;
import com.techtraining.evaluationservice.entity.EvaluationAssignment;
import com.techtraining.evaluationservice.enums.AssignmentStatus;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-08T23:01:51+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Microsoft)"
)
public class AssignmentMapperImpl implements AssignmentMapper {

    @Override
    public AssignmentResponse toResponse(EvaluationAssignment assignment) {
        if ( assignment == null ) {
            return null;
        }

        AssignmentResponse assignmentResponse = new AssignmentResponse();

        assignmentResponse.setId( assignment.getId() );
        assignmentResponse.setEnrollmentId( assignment.getEnrollmentId() );
        assignmentResponse.setEvaluatorId( assignment.getEvaluatorId() );
        assignmentResponse.setRoundNumber( assignment.getRoundNumber() );
        assignmentResponse.setStatus( assignment.getStatus() );
        assignmentResponse.setAssignedAt( assignment.getAssignedAt() );

        return assignmentResponse;
    }

    @Override
    public EvaluationAssignment toEntity(AssignmentRequest request) {
        if ( request == null ) {
            return null;
        }

        EvaluationAssignment.EvaluationAssignmentBuilder evaluationAssignment = EvaluationAssignment.builder();

        evaluationAssignment.enrollmentId( request.getEnrollmentId() );
        evaluationAssignment.evaluatorId( request.getEvaluatorId() );
        evaluationAssignment.roundNumber( request.getRoundNumber() );

        evaluationAssignment.status( AssignmentStatus.PENDING );

        return evaluationAssignment.build();
    }

    @Override
    public List<AssignmentResponse> toResponseList(List<EvaluationAssignment> assignments) {
        if ( assignments == null ) {
            return null;
        }

        List<AssignmentResponse> list = new ArrayList<AssignmentResponse>( assignments.size() );
        for ( EvaluationAssignment evaluationAssignment : assignments ) {
            list.add( toResponse( evaluationAssignment ) );
        }

        return list;
    }
}
