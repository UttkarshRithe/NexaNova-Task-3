package com.techtraining.evaluationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(
        name = "participant-service",
        url = "http://participant-service:8084"
)
public interface ParticipantClient {

    @GetMapping("/api/enrollments/{id}")
    Map<String, Object> getEnrollmentById(
            @PathVariable("id") Long id
    );
}