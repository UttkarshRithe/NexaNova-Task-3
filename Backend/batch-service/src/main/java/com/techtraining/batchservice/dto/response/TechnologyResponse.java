package com.techtraining.batchservice.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TechnologyResponse {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
}
