package com.techtraining.auth.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/users/by-email")
    UserResponse getUserByEmail(@RequestParam("email") String email);

    @Data
    class UserResponse {
        private Long id;
        private String name;
        private String email;
        private String passwordHash;
        private String role;
        private Boolean isActive;
    }
}
