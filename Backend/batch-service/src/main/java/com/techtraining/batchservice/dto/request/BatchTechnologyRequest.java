package com.techtraining.batchservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BatchTechnologyRequest {
    @NotNull(message = "Batch ID is required")
    private Long batchId;

    @NotNull(message = "Technology ID is required")
    private Long technologyId;

    @NotNull(message = "Total rounds is required")
    @Min(value = 1, message = "Total rounds must be at least 1")
    private Integer totalRounds;
}
