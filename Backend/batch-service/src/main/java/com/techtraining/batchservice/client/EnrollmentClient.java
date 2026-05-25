package com.techtraining.batchservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "participant-service",
        url = "http://participant-service:8084"
)
public interface EnrollmentClient {

    @GetMapping("/api/enrollments/internal/batch-technology/{btId}")
    List<Map<String, Object>> getEnrollmentsByBatchTechnologyInternal(@PathVariable("btId") Long btId);
}
