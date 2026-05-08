package com.techtraining.reportservice.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "batch-service")
public interface BatchClient {
    @GetMapping("/api/batches/{id}")
    BatchResponse getBatchById(@PathVariable("id") Long id);

    @GetMapping("/api/technologies/{id}")
    TechnologyResponse getTechnologyById(@PathVariable("id") Long id);

    @GetMapping("/api/batch-technology/batch/{batchId}")
    List<BatchTechnologyResponse> getTechnologiesByBatchId(@PathVariable("batchId") Long batchId);

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
    }
}
