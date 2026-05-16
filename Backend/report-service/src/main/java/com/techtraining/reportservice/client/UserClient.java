package com.techtraining.reportservice.client;

import com.techtraining.reportservice.config.FeignConfig;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.techtraining.common.dto.ApiResponse;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserClient {
    @GetMapping("/api/users/{id}")
    ApiResponse<UserResponse> getUserById(@PathVariable("id") Long id);

    @Data
    class UserResponse {
        private Long id;
        private String name;
    }
}
