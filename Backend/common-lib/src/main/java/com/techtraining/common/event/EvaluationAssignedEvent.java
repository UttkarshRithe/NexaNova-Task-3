package com.techtraining.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationAssignedEvent {

    private Long assignmentId;

    private String participantName;
    private String participantEmail;

    private String batchName;
    private String technologyName;

    private Integer roundNumber;

    private String evaluatorName;

    private LocalDate evaluationDate;
    private String evaluationTime;

    private String meetingLink;

}
