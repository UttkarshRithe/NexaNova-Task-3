package com.techtraining.userservice.service.impl;

import com.techtraining.common.constants.AppConstants;
import com.techtraining.common.exception.DuplicateResourceException;
import com.techtraining.common.exception.ResourceNotFoundException;
import com.techtraining.userservice.dto.request.UserRequest;
import com.techtraining.userservice.dto.response.InternalUserResponse;
import com.techtraining.userservice.dto.response.UserResponse;
import com.techtraining.userservice.entity.User;
import com.techtraining.userservice.enums.UserRole;
import com.techtraining.userservice.mapper.UserMapper;
import com.techtraining.userservice.repository.UserRepository;
import com.techtraining.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final com.techtraining.userservice.client.AssignmentClient assignmentClient;

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User already exists with email: " + request.getEmail());
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND + id));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmailAndStatus(email, com.techtraining.common.enums.EntityStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findByStatus(com.techtraining.common.enums.EntityStatus.ACTIVE, pageable).map(userMapper::toResponse);
    }

    @Override
    public Page<UserResponse> getEvaluators(Pageable pageable) {
        return userRepository.findByRoleAndStatus(UserRole.EVALUATOR, com.techtraining.common.enums.EntityStatus.ACTIVE, pageable).map(userMapper::toResponse);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND + id));
        
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // Do not update password here
        
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void softDeleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND + id));
        
        if (user.getRole() == UserRole.EVALUATOR) {
            try {
                if (assignmentClient.hasPendingAssignments(id)) {
                    throw new IllegalStateException("Cannot delete evaluator: future assignments exist.");
                }
            } catch (IllegalStateException e) {
                throw e;
            } catch (Exception e) {
                // If service is down, log it
            }
        }
        
        user.setStatus(com.techtraining.common.enums.EntityStatus.ARCHIVED);
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND + id));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public InternalUserResponse getUserByEmailInternal(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        InternalUserResponse response = new InternalUserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPasswordHash(user.getPasswordHash());
        response.setRole(user.getRole());
        response.setIsActive(user.getIsActive());
        return response;
    }
}
