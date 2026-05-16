package com.techtraining.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchAnalysisRequest {

    @NotBlank
    private String batchName;

    @NotNull
    private Integer passingScore;

    @NotEmpty
    private List<TechnologyData> technologies;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TechnologyData {
        private String name;
        private Integer totalRounds;
        private Integer participantCount;
        private Double avgScore;
        private Double highestScore;
        private Double lowestScore;
        private Integer atRiskCount;
        private List<Double> roundAverages;
        private DistributionData distribution;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistributionData {
        private Integer excellent;
        private Integer average;
        private Integer poor;
    }
}

