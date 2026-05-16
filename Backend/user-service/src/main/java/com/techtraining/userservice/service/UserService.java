package com.techtraining.userservice.service;

import com.techtraining.userservice.dto.request.UserRequest;
import com.techtraining.userservice.dto.response.InternalUserResponse;
import com.techtraining.userservice.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserById(Long id);
    UserResponse getUserByEmail(String email);
    Page<UserResponse> getAllUsers(Pageable pageable);
    Page<UserResponse> getEvaluators(Pageable pageable);
    UserResponse updateUser(Long id, UserRequest request);
    void softDeleteUser(Long id);
    void resetPassword(Long id, String newPassword);
    InternalUserResponse getUserByEmailInternal(String email);
}
