package com.techtraining.participantservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollmentRequest {
    @NotNull(message = "Participant ID is required")
    private Long participantId;

    @NotNull(message = "Batch Technology ID is required")
    private Long batchTechnologyId;
}
