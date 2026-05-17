package com.techtraining.reportservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(
        scanBasePackages = {
                "com.techtraining.reportservice",
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
                "com.techtraining.reportservice.client"
        }
)

public class ReportServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                ReportServiceApplication.class,
                args
        );
    }
}
