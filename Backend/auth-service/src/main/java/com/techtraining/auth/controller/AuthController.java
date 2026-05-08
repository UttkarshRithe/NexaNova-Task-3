package com.techtraining.auth.controller;

import com.techtraining.auth.dto.AuthResponse;
import com.techtraining.auth.dto.LoginRequest;
import com.techtraining.auth.service.AuthService;
import com.techtraining.common.constants.AppConstants;
import com.techtraining.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.LOGIN_SUCCESS, response));
    }
}
