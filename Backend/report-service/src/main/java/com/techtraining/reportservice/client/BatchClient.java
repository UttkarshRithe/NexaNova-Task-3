package com.techtraining.reportservice.client;

import com.techtraining.reportservice.config.FeignConfig;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import com.techtraining.common.dto.ApiResponse;

@FeignClient(name = "batch-service", configuration = FeignConfig.class)
public interface BatchClient {
    @GetMapping("/api/batches/{id}")
    ApiResponse<BatchResponse> getBatchById(@PathVariable("id") Long id);

    @GetMapping("/api/technologies/{id}")
    ApiResponse<TechnologyResponse> getTechnologyById(@PathVariable("id") Long id);

    @GetMapping("/api/batch-technology/batch/{batchId}")
    ApiResponse<List<BatchTechnologyResponse>> getTechnologiesByBatchId(@PathVariable("batchId") Long batchId);

    @GetMapping("/api/batch-technology/{id}")
    ApiResponse<BatchTechnologyResponse> getBatchTechnologyById(@PathVariable("id") Long id);

    @Data
    class BatchResponse {
        private Long id;
        private String name;
    }

    @Data
    class TechnologyResponse {
        private Long id;
        private String name;
    }

    @Data
    class BatchTechnologyResponse {
        private Long id;
        private Long batchId;
        private Long technologyId;
        private Integer totalRounds;
    }
}
