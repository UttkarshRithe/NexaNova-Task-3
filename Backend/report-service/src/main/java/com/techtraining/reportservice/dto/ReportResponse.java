package com.techtraining.reportservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReportResponse {
    private String batchName;
    private String technologyName;
    private List<ParticipantScore> scores;

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
    }
}
