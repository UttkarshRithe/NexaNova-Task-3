package com.techtraining.reportservice.client;

import lombok.Data;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import com.techtraining.common.dto.ApiResponse;

@FeignClient(name = "evaluation-service")
public interface EvaluationClient {

    @GetMapping("/api/evaluation-assignments/enrollment/{id}")
    ApiResponse<List<AssignmentResponse>> getAssignmentsByEnrollment(
            @PathVariable("id") Long id
    );

    @GetMapping("/api/evaluation-results/assignment/{assignmentId}")
    ApiResponse<ResultResponse> getResultByAssignment(
            @PathVariable("assignmentId") Long assignmentId
    );

    @Data
    class AssignmentResponse {

        private Long id;

        private Long enrollmentId;

        private Long evaluatorId;

        private Integer roundNumber;

        private String status;
    }

    @Data
    class ResultResponse {

        private Long id;

        private Long assignmentId;

        private Integer score;

        private String comments;

        private String aiFeedback;

        private String strengths;

        private String weaknesses;
    }
}
