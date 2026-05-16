package com.techtraining.reportservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndividualReportResponse {
    private Long participantId;
    private String participantName;
    private String participantEmail;
    private String batchName;
    private String technologyName;
    private Integer roundsCompleted;
    private Integer totalRounds;
    
    private Double averageScore;
    private Double technicalScore;
    private Double communicationScore;
    private Double problemSolvingScore;
    
    private List<RoundDetail> rounds;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoundDetail {
        private Integer roundNumber;
        private String technology;
        private Integer score;
        private String evaluatorName;
        private LocalDateTime submittedAt;
        private String feedback;
        private Integer previousScore;
        private String status;
    }
}
