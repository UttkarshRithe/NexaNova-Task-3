package com.techtraining.participantservice.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnrollmentResponse {
    private Long id;
    private Long participantId;
    private String participantName;
    private Long batchTechnologyId;
    private LocalDateTime enrolledAt;
}
