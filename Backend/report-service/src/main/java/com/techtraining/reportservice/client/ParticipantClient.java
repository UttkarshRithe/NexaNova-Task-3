package com.techtraining.reportservice.client;

import com.techtraining.reportservice.config.FeignConfig;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import com.techtraining.common.dto.ApiResponse;

@FeignClient(name = "participant-service", configuration = FeignConfig.class)
public interface ParticipantClient {
    @GetMapping("/api/enrollments/batch-technology/{btId}")
    ApiResponse<List<EnrollmentResponse>> getEnrollmentsByBatchTechnology(@PathVariable("btId") Long btId);

    @GetMapping("/api/enrollments/participant/{participantId}")
    ApiResponse<List<EnrollmentResponse>> getEnrollmentsByParticipant(@PathVariable("participantId") Long participantId);

    @GetMapping("/api/participants/{id}")
    ApiResponse<ParticipantResponse> getParticipantById(@PathVariable("id") Long id);

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
