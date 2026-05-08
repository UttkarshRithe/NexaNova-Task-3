package com.techtraining.evaluationservice.controller;

import com.techtraining.common.constants.AppConstants;
import com.techtraining.common.dto.ApiResponse;
import com.techtraining.evaluationservice.dto.request.ResultRequest;
import com.techtraining.evaluationservice.dto.response.ResultResponse;
import com.techtraining.evaluationservice.service.ResultService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluation-results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    @PostMapping
    @PreAuthorize("hasRole('EVALUATOR')")
    public ResponseEntity<ApiResponse<ResultResponse>> submitResult(@Valid @RequestBody ResultRequest request, HttpServletRequest servletRequest) {
        Long evaluatorId = Long.parseLong(servletRequest.getHeader("X-User-Id"));
        ResultResponse response = resultService.submitResult(request, evaluatorId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(AppConstants.EVAL_SUBMITTED, response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResultResponse>> getResult(@PathVariable Long id) {
        ResultResponse response = resultService.getResultById(id);
        return ResponseEntity.ok(ApiResponse.success("Result fetched successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EVALUATOR')")
    public ResponseEntity<ApiResponse<ResultResponse>> updateResult(@PathVariable Long id, @Valid @RequestBody ResultRequest request, HttpServletRequest servletRequest) {
        Long evaluatorId = Long.parseLong(servletRequest.getHeader("X-User-Id"));
        ResultResponse response = resultService.updateResult(id, request, evaluatorId);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.EVAL_UPDATED, response));
    }

    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<ApiResponse<ResultResponse>> getResultByAssignment(@PathVariable Long assignmentId) {
        ResultResponse response = resultService.getResultByAssignment(assignmentId);
        return ResponseEntity.ok(ApiResponse.success("Result for assignment fetched successfully", response));
    }
}
