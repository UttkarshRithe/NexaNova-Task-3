package com.techtraining.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {
        "com.techtraining.gateway",
        "com.techtraining.common"
})
@EnableDiscoveryClient
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.cloud.gateway.filter.ratelimit.KeyResolver userKeyResolver() {
        return exchange -> reactor.core.publisher.Mono.just(
            exchange.getRequest().getRemoteAddress() != null 
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() 
                : "unknown"
        );
    }
}
