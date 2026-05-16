package com.techtraining.evaluationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(
        name = "participant-service",
        url = "http://participant-service:8084",
        fallback = ParticipantClientFallback.class
)
public interface ParticipantClient {

    @GetMapping("/api/enrollments/internal/{id}")
    Map<String, Object> getEnrollmentById(
            @PathVariable("id") Long id
    );

    @GetMapping("/api/participants/internal/{id}")
    Map<String, Object> getParticipantById(
            @PathVariable("id") Long id
    );

    @GetMapping("/api/enrollments/internal/email-by-enrollment/{id}")
    String getParticipantEmailByEnrollmentId(
            @PathVariable("id") Long id
    );
}