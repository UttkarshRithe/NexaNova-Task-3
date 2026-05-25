package com.techtraining.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "evaluation-service",
        url = "http://evaluation-service:8085"
)
public interface AssignmentClient {

    @GetMapping("/api/evaluation-assignments/internal/evaluator/{evaluatorId}/has-pending")
    boolean hasPendingAssignments(@PathVariable("evaluatorId") Long evaluatorId);
}
