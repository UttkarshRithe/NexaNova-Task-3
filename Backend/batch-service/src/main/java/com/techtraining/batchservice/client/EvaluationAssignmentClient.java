package com.techtraining.batchservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "evaluation-service",
        url = "http://evaluation-service:8085"
)
public interface EvaluationAssignmentClient {

    @GetMapping("/api/evaluation-assignments/internal/enrollment/{id}")
    List<Map<String, Object>> getAssignmentsByEnrollmentInternal(@PathVariable("id") Long enrollmentId);
}
