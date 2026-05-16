package com.techtraining.notificationservice.service;

import com.techtraining.common.event.EvaluationAssignedEvent;
import com.techtraining.common.event.ParticipantReportEmailEvent;

public interface EmailService {
    void sendAssignmentEmail(EvaluationAssignedEvent event);
    void sendParticipantReport(ParticipantReportEmailEvent event);
}
