package com.techtraining.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantReportEmailEvent {

    private Long participantId;
    private String participantName;
    private String participantEmail;
    private String batchName;
    private String technologyName;
    private String pdfFilePath;

}
