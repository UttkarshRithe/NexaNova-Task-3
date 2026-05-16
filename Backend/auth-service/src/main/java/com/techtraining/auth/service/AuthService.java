package com.techtraining.auth.service;

import com.techtraining.auth.client.UserClient;
import com.techtraining.auth.dto.AuthResponse;
import com.techtraining.auth.dto.LoginRequest;
import com.techtraining.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserClient userClient;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {


        UserClient.UserResponse user = userClient.getUserByEmail(request.getEmail());

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole())
                .name(user.getName())
                .build();
    }

}
