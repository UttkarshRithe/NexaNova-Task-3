package com.techtraining.reportservice.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "participant-service")
public interface ParticipantClient {
    @GetMapping("/api/enrollments/batch-technology/{btId}")
    List<EnrollmentResponse> getEnrollmentsByBatchTechnology(@PathVariable("btId") Long btId);

    @GetMapping("/api/participants/{id}")
    ParticipantResponse getParticipantById(@PathVariable("id") Long id);

    @Data
    class EnrollmentResponse {
        private Long id;
        private Long participantId;
        private Long batchTechnologyId;
    }

    @Data
    class ParticipantResponse {
        private Long id;
        private String name;
        private String email;
    }
}
