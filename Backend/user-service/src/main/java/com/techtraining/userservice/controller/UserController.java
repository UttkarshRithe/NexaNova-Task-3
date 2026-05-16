package com.techtraining.userservice.controller;

import com.techtraining.common.constants.AppConstants;
import com.techtraining.common.dto.ApiResponse;
import com.techtraining.userservice.dto.request.UserRequest;
import com.techtraining.userservice.dto.response.InternalUserResponse;
import com.techtraining.userservice.dto.response.UserResponse;
import com.techtraining.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserRequest request
    ) {

        UserResponse response =
                userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                AppConstants.USER_CREATED,
                                response
                        )
                );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long id
    ) {

        UserResponse response =
                userService.getUserById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User fetched successfully",
                        response
                )
        );
    }

    // ✅ INTERNAL API FOR MICROSERVICES
    @GetMapping("/internal/{id}")
    public UserResponse getUserInternal(
            @PathVariable Long id
    ) {

        return userService.getUserById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            Pageable pageable
    ) {

        Page<UserResponse> response =
                userService.getAllUsers(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Users fetched successfully",
                        response
                )
        );
    }

    @GetMapping("/evaluators")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getEvaluators(Pageable pageable) {

        Page<UserResponse> response =
                userService.getEvaluators(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Evaluators fetched successfully",
                        response
                )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request
    ) {

        UserResponse response =
                userService.updateUser(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        AppConstants.USER_UPDATED,
                        response
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id
    ) {

        userService.softDeleteUser(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        AppConstants.USER_DELETED,
                        null
                )
        );
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable Long id,
            @RequestBody String newPassword
    ) {

        userService.resetPassword(id, newPassword);

        return ResponseEntity.ok(
                ApiResponse.success(
                        AppConstants.PASSWORD_RESET,
                        null
                )
        );
    }

    @GetMapping("/by-email")
    public InternalUserResponse getUserByEmail(
            @RequestParam String email
    ) {

        return userService.getUserByEmailInternal(email);
    }
}