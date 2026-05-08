package com.techtraining.evaluationservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
@OpenAPIDefinition(
        info = @Info(title = "Auth Service API", version = "v1"),
        servers = {@Server(url = "http://localhost:9900")}
)
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(
        basePackages = "com.techtraining.evaluationservice.client"
)
public class EvaluationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EvaluationServiceApplication.class, args);
    }
}
