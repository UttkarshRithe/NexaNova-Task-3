package com.techtraining.reportservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReportResponse {
    private String batchName;
    private String participantName; // Used for individual reports
    private List<TechnologyReport> technologies;

    @Data
    @Builder
    public static class TechnologyReport {
        private String technologyName;
        private List<ParticipantScore> participantScores;
    }

    @Data
    @Builder
    public static class ParticipantScore {
        private String participantName;
        private List<RoundScore> rounds;
        private Double averageScore;
    }

    @Data
    @Builder
    public static class RoundScore {
        private Integer roundNumber;
        private Integer score;
        private String evaluatorName;
        private String feedback;
    }
}
