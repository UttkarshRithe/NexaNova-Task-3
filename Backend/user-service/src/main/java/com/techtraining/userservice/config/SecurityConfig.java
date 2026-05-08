package com.techtraining.userservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final InternalJwtFilter internalJwtFilter;

    public SecurityConfig(InternalJwtFilter internalJwtFilter) {
        this.internalJwtFilter = internalJwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // ✅ INTERNAL AUTH-SERVICE ACCESS
                        .requestMatchers("/api/users/by-email").permitAll()

                        // ✅ SWAGGER
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**",
                                "/api/users/internal/**"
                        ).permitAll()

                        // 🔒 EVERYTHING ELSE REQUIRES AUTH
                        .anyRequest().authenticated()
                )

                // ✅ TRUST GATEWAY HEADERS
                .addFilterBefore(
                        internalJwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}