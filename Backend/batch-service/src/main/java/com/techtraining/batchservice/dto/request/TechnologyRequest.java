package com.techtraining.batchservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TechnologyRequest {
    @NotBlank(message = "Technology name is required")
    private String name;
}
