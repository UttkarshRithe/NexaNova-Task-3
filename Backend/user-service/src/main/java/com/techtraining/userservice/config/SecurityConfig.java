package com.techtraining.userservice.config;

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

                // ✅ Disable default spring security login
                .csrf(csrf -> csrf.disable())

                .httpBasic(httpBasic -> httpBasic.disable())

                .formLogin(formLogin -> formLogin.disable())

                // ✅ Stateless JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // ✅ INTERNAL AUTH SERVICE ACCESS
                        .requestMatchers("/api/users/by-email").permitAll()

                        // ✅ INTERNAL MICROSERVICE CALLS
                        .requestMatchers("/api/users/internal/**").permitAll()

                        // ✅ SWAGGER
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**"
                        ).permitAll()

                        // 🔒 EVERYTHING ELSE NEEDS AUTH
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
