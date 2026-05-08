package com.techtraining.participantservice.client;

import com.techtraining.participantservice.config.FeignConfig;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(
        name = "batch-service",
        url = "http://batch-service:8082",
        configuration = FeignConfig.class
)
public interface BatchClient {

    @GetMapping("/api/batch-technology/{id}")
    Map<String, Object> getBatchTechnology(
            @PathVariable("id") Long id
    );
}