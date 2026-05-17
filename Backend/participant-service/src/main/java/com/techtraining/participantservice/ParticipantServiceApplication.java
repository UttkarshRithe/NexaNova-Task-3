package com.techtraining.participantservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@OpenAPIDefinition(
        info = @Info(
                title = "Participant Service API",
                version = "v1"
        ),
        servers = {
                @Server(url = "http://localhost:9900")
        }
)

@SpringBootApplication(
        scanBasePackages = {
                "com.techtraining.participantservice",
                "com.techtraining.common"
        },
        exclude = {
                SecurityAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class
        }
)

@EnableDiscoveryClient

@EnableFeignClients(
        basePackages = {
                "com.techtraining.participantservice.client"
        }
)

public class ParticipantServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                ParticipantServiceApplication.class,
                args
        );
    }
}
