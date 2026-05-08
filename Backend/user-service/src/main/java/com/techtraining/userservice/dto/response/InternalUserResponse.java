package com.techtraining.userservice.dto.response;

import com.techtraining.userservice.enums.UserRole;
import lombok.Data;

@Data
public class InternalUserResponse {
    private Long id;
    private String name;
    private String email;
    private String passwordHash;
    private UserRole role;
    private Boolean isActive;
}