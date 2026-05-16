package com.techtraining.ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAnalysisResponse {
    private String overallHealth;
    private String aiSummary;
    private String recommendation;
    private List<TechnologySummary> technologySummaries;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TechnologySummary {
        private String name;
        private String status;
        private Double avgScore;
        private Integer atRiskCount;
        private String trend;
    }
}

