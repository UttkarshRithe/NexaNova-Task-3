package com.techtraining.participantservice.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {

        return template -> {

            ServletRequestAttributes attributes =
                    (ServletRequestAttributes)
                            RequestContextHolder.getRequestAttributes();

            if (attributes != null) {

                HttpServletRequest request =
                        attributes.getRequest();

                // ✅ Forward gateway headers
                String userId =
                        request.getHeader("X-User-Id");

                String userEmail =
                        request.getHeader("X-User-Email");

                String userRole =
                        request.getHeader("X-User-Role");

                if (userId != null) {
                    template.header("X-User-Id", userId);
                }

                if (userEmail != null) {
                    template.header("X-User-Email", userEmail);
                }

                if (userRole != null) {
                    template.header("X-User-Role", userRole);
                }
            }
        };
    }
}