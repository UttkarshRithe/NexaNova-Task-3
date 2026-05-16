package com.techtraining.evaluationservice.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResultResponse {
    private Long id;
    private Long assignmentId;
    private Integer score;
    private Integer technicalScore;
    private Integer communicationScore;
    private Integer problemSolvingScore;
    private String comments;
    private String strengths;
    private String weaknesses;
    private String aiFeedback;
    private LocalDateTime submittedAt;
}
