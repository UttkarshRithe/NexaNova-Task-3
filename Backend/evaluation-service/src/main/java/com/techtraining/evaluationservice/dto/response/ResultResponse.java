package com.techtraining.evaluationservice.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResultResponse {
    private Long id;
    private Long assignmentId;
    private Integer score;
    private String comments;
    private LocalDateTime submittedAt;
}
