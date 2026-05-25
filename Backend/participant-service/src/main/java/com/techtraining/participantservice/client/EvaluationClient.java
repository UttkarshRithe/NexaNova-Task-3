package com.techtraining.participantservice.client;

import com.techtraining.participantservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;

@FeignClient(
        name = "evaluation-service",
        url = "http://evaluation-service:8085",
        configuration = FeignConfig.class
)
public interface EvaluationClient {

    @DeleteMapping("/api/evaluation-assignments/internal/enrollment/{id}")
    void deleteAssignmentsByEnrollment(
            @PathVariable("id") Long enrollmentId,
            @RequestHeader("X-Internal-Secret") String internalSecret
    );

    @GetMapping("/api/evaluation-assignments/internal/enrollment/{id}")
    List<Map<String, Object>> getAssignmentsByEnrollmentInternal(@PathVariable("id") Long enrollmentId);
}
