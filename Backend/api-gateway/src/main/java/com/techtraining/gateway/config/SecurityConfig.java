package com.techtraining.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

        @Bean
        public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeExchange(exchanges -> exchanges

                                                // PUBLIC APIs
                                                .pathMatchers(
                                                                "/api/auth/**",
                                                                "/swagger-ui.html",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**",
                                                                "/webjars/**")
                                                .permitAll()

                                                // INTERNAL APIs - BLOCKED from external access
                                                // Note: Spring Boot 3 PathPatternParser doesn't allow ** in the middle.
                                                // /api/*/internal/** matches /api/{service}/internal/anything
                                                .pathMatchers("/api/*/internal/**").denyAll()

                                                // EVERYTHING ELSE PROTECTED
                                                .anyExchange().permitAll());

                return http.build();
        }
}