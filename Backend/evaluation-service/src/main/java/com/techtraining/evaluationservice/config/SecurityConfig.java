package com.techtraining.evaluationservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final InternalJwtFilter internalJwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers(
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/webjars/**"
                    ).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/evaluation-assignments/my").hasRole("EVALUATOR")
                .requestMatchers(HttpMethod.POST, "/api/evaluation-results").hasRole("EVALUATOR")
                .requestMatchers(HttpMethod.PUT, "/api/evaluation-results/**").hasRole("EVALUATOR")
                .requestMatchers(HttpMethod.POST, "/api/evaluation-assignments").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/evaluation-assignments/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/evaluation-assignments/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(internalJwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
