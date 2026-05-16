package com.techtraining.reportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchAnalysisRequest {
    private String batchName;
    private Integer passingScore;
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

