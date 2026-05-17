package com.techtraining.participantservice.client;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "batch-service")
public interface BatchClient {

    @GetMapping("/api/batch-technology/{id}")
    Map<String, Object> getBatchTechnology(
            @PathVariable("id") Long id
    );
}
