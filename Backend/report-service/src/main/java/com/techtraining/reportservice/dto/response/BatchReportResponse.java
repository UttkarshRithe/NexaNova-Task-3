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
public class BatchReportResponse {
    private String batchName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer totalTechnologies;
    private Integer totalParticipants;
    private Integer completedCount;
    private Integer pendingCount;
    
    private List<TechnologyGroup> technologies;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TechnologyGroup {
        private String technologyName;
        private Integer totalRounds;
        private Integer enrolledCount;
        private List<AssignmentRow> assignments;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignmentRow {
        private String participantName;
        private Integer roundNumber;
        private Integer score;
        private String evaluatorName;
        private LocalDateTime submittedAt;
        private String status;
        private String feedback;
    }
}
