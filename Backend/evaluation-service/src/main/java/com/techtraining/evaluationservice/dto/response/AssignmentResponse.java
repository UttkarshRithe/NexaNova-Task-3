package com.techtraining.evaluationservice.dto.response;

import com.techtraining.evaluationservice.enums.AssignmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentResponse {
    private Long id;
    private Long enrollmentId;
    private String participantName;
    private String batchName;
    private String technologyName;
    private Long evaluatorId;
    private String evaluatorName;
    private Integer roundNumber;
    private AssignmentStatus status;
    private LocalDateTime assignedAt;
}
