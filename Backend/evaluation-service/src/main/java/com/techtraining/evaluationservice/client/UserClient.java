package com.techtraining.evaluationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(
        name = "user-service",
        url = "http://user-service:8083",
        fallback = UserClientFallback.class
)
public interface UserClient {

    @GetMapping("/api/users/internal/{id}")
    Map<String, Object> getUserById(
            @PathVariable("id") Long id
    );
}