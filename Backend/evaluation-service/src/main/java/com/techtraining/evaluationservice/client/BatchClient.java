package com.techtraining.evaluationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(
        name = "batch-service",
        url = "http://batch-service:8082",
        fallback = BatchClientFallback.class
)
public interface BatchClient {

    @GetMapping("/api/batch-technology/internal/{id}")
    Map<String, Object> getBatchTechnologyById(
            @PathVariable("id") Long id
    );
}
