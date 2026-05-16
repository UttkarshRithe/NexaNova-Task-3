package com.techtraining.ai.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {

    @NotBlank
    private String technology;

    @NotNull
    @Min(1)
    private Integer roundNumber;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer overallScore;

    private Integer technicalScore;
    private Integer communicationScore;
    private Integer problemSolvingScore;

    private String strengths;
    private String weaknesses;

    @NotBlank
    private String evaluatorComment;

    private String difficultyLevel;
}

