package com.techtraining.evaluationservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResultRequest {
    @NotNull(message = "Assignment ID is required")
    private Long assignmentId;

    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score cannot be less than 0")
    @Max(value = 100, message = "Score cannot exceed 100")
    private Integer score;

    @Size(max = 1000, message = "Comments must not exceed 1000 characters")
    private String comments;
}
