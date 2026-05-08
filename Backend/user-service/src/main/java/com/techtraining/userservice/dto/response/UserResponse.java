package com.techtraining.userservice.dto.response;

import com.techtraining.userservice.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
